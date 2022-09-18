package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.util.command.Commands;
import com.ftd.telegramhelper.util.message.MessageUtils;
import com.ftd.telegramhelper.util.request.RequestHelper;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ftd.telegramhelper.util.message.MessageUtils.isImage;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private final FeedbackService feedbackService;
    private final RequestHelper requestHelper;
    private final Commands commands;

    @Autowired
    public MessageHandlerImpl(
            TelegramUserService telegramUserService,
            ResponseHelper responseHelper,
            FeedbackChannelConfig feedbackChannelConfig,
            FeedbackService feedbackService,
            RequestHelper requestHelper, Commands commands) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
        this.feedbackService = feedbackService;
        this.requestHelper = requestHelper;
        this.commands = commands;
    }

    @Override
    public PartialBotApiMethod<?> processMessage(Message message)
            throws TelegramApiException, IncorrectFeedbackChannelPostException {
        Long chatId = message.getChatId();
        String chatIdAsString = String.valueOf(chatId);

        Command command = Command.create(message.getText());
        User user = message.getFrom();

        if (command.getMessage() != null && commands.getStartCommand().equals(command)) {
            getOrCreateTelegramUser(user, chatId);
            return responseHelper.createMainMenu(chatIdAsString, true);
        } else if (command.getMessage() != null && commands.getKnownCommands().contains(command)) {
            return responseHelper.updateReplyKeyboardMarkup(
                    getOrCreateTelegramUser(user, chatId),
                    chatIdAsString,
                    command
            );
        } else if (isMessageFromFeedbackChat(message)) {
            processMessageFromFeedbackChannel(message);
        } else {
            updateFeedbackFor(user, message);
        }

        return null;
    }

    private void processMessageFromFeedbackChannel(Message message)
            throws TelegramApiException, IncorrectFeedbackChannelPostException {
        User messageFrom = message.getFrom();
        Long telegramUserId = MessageUtils.getTelegramUserIdFromComment(message);
        if (isTelegramBot(messageFrom)) {
            // set feedbackMessageId from feedback post when got update from channel which has been sent by our bot
            if (telegramUserId == null) {
                throw new IncorrectFeedbackChannelPostException();
            } else {
                TelegramUser telegramUser = telegramUserService.findBy(telegramUserId);
                if (telegramUser.getFeedbackMessageId() == null) {
                    telegramUser.setFeedbackMessageId(String.valueOf(message.getMessageId()));
                    telegramUserService.save(telegramUser);
                }
                return;
            }
        }

        if (telegramUserId == null) {
            responseHelper.handleError(message.getChatId());
            throw new IncorrectFeedbackChannelPostException();
        }

        TelegramUser existingUser = telegramUserService.findBy(telegramUserId);
        if (existingUser != null) {
            responseHelper.sendMessage(
                    String.valueOf(existingUser.getChatId()),
                    message.getText()
            );
        }
    }

    /**
     * Hardcoded and may be unstable.
     */
    private boolean isTelegramBot(User user) {
        return user.getUserName() == null && user.getFirstName().equals("Telegram"); //&& user.getId() == 777000L;
    }

    private void updateFeedbackFor(User user, Message message) throws TelegramApiException {
        TelegramUser telegramUser = telegramUserService.findBy(user.getId());
        if (telegramUser != null && UserStates.CAN_SEND_MESSAGES.equals(telegramUser.getState())) {
            if (message.hasPhoto()) {
                feedbackService.updateFeedback(telegramUser, requestHelper.getPhotoFrom(message), false);
            }
            if (message.hasDocument() && isImage(message.getDocument())) {
                feedbackService.updateFeedback(telegramUser, requestHelper.getPhotoFrom(message), true);
            }
            if (message.hasText()) {
                feedbackService.updateFeedback(
                        telegramUser,
                        "Message from user: " + message.getText()
                );
            }
            responseHelper.sendMessage(
                    String.valueOf(message.getChatId()),
                    "[INFO]: Ваше сообщение успешно отправлено"
            );
        } else {
            responseHelper.handleError(message.getChatId());
            logger.info(user + " does not have the required status to send the message");
        }
    }

    private boolean isMessageFromFeedbackChat(Message message) {
        String chatName = message.getChat().getUserName();
        return StringUtils.hasText(chatName) && chatName.equals(
                feedbackChannelConfig.getChannelChatId().replace("@", "")
        );
    }

    private TelegramUser getOrCreateTelegramUser(User user, Long chatId) {
        TelegramUser existingUser = telegramUserService.findBy(user.getId());
        return existingUser == null
                ? telegramUserService.createAndSaveFrom(user, chatId)
                : existingUser;
    }
}
