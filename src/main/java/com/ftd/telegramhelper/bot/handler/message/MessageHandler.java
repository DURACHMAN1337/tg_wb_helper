package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.bot.meta.command.Command;
import com.ftd.telegramhelper.bot.meta.value.command.CommandValue;
import com.ftd.telegramhelper.bot.service.command.CommandService;
import com.ftd.telegramhelper.config.bot.feedbackchannel.FeedbackChannelConfig;
import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ftd.telegramhelper.util.message.MessageUtils.isImage;

@Component
public class MessageHandler implements IMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private final FeedbackService feedbackService;
    private final RequestHelper requestHelper;
    private final CommandService commandService;

    @Autowired
    public MessageHandler(
            TelegramUserService telegramUserService,
            ResponseHelper responseHelper,
            FeedbackChannelConfig feedbackChannelConfig,
            FeedbackService feedbackService,
            RequestHelper requestHelper, CommandService commandService) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
        this.feedbackService = feedbackService;
        this.requestHelper = requestHelper;
        this.commandService = commandService;
    }

    @Override
    public BotApiMethod<?> processMessage(Message message)
            throws TelegramApiException, IncorrectFeedbackChannelPostException {
        Command command = Command.create(CommandValue.create(message.getText()));
        if (commandService.isKnownCommand(command)) {
            return doProcessCommand(message, command);
        } else {
            return doProcessMessage(message);
        }
    }

    private BotApiMethod<?> doProcessCommand(Message message, Command command) {
        if (commandService.isStartCommand(command)) {
            createTelegramUserIfNotExist(message.getFrom(), message.getChatId());
            return responseHelper.createMainMenu(String.valueOf(message.getChatId()), true);
        } else {
            // add code here
        }
        return null;
    }

    private BotApiMethod<?> doProcessMessage(Message message)
            throws TelegramApiException, IncorrectFeedbackChannelPostException {
        if (isMessageFromFeedbackChat(message)) {
            processMessageFromFeedbackChannel(message);
        } else {
            TelegramUser telegramUser = telegramUserService.findBy(message.getFrom().getId());
            if (telegramUser == null) {
                throw new RuntimeException("Telegram user not found");
            } else if (UserStates.CAN_SEND_MESSAGES.equals(telegramUser.getState())) {
                updateFeedbackFor(telegramUser, message);
            } else {
                responseHelper.handleError(String.valueOf(message.getChatId()));
                logger.info(telegramUser + " does not have the required status to send the message");
            }
        }

        return null;
    }

    private void processMessageFromFeedbackChannel(Message message)
            throws TelegramApiException, IncorrectFeedbackChannelPostException {
        User messageFrom = message.getFrom();
        Long telegramUserId = MessageUtils.getTelegramUserIdFromFeedbackPost(message);
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
            responseHelper.handleError(String.valueOf(message.getChatId()));
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

    private void updateFeedbackFor(TelegramUser telegramUser, Message message) throws TelegramApiException {
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

    }

    private boolean isMessageFromFeedbackChat(Message message) {
        String chatName = message.getChat().getUserName();
        return StringUtils.hasText(chatName) && chatName.equals(
                feedbackChannelConfig.getChannelChatId().replace("@", "")
        );
    }

    private void createTelegramUserIfNotExist(User user, Long chatId) {
        TelegramUser existingUser = telegramUserService.findBy(user.getId());
        if (existingUser == null) {
            telegramUserService.createAndSaveFrom(user, chatId);
        }
    }
}
