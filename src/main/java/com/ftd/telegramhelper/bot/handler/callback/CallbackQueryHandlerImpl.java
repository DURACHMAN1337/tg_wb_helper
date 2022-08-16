package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.ftd.telegramhelper.util.message.MessageService.getTelegramUserIdFromComment;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final FeedbackService feedbackService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private final TelegramUserService telegramUserService;

    @Autowired
    public CallbackQueryHandlerImpl(FeedbackService feedbackService, ResponseHelper responseHelper, FeedbackChannelConfig feedbackChannelConfig, TelegramUserService telegramUserService) {
        this.feedbackService = feedbackService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        TelegramUser telegramUser = getSuitableTelegramUser(callbackQuery,callbackQuery.getMessage());
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = String.valueOf(telegramUser.getChatId());
        if (Callback.FIRST.equals(callbackQuery.getData())) {
            try {

                feedbackService.updateFeedback(telegramUser, null);
                telegramUser.setState(UserStates.CAN_SEND_MESSAGES);
                telegramUserService.save(telegramUser);
                return responseHelper.createInstructionMessage(chatId, messageId);
            } catch (Exception e) {
                return responseHelper.createErrorResponse(callbackQuery);
            }
        }
        if (Callback.SECOND.equals(callbackQuery.getData())) {
            telegramUser.setState(UserStates.IN_PROGRESS);
            telegramUserService.save(telegramUser);
            return responseHelper.createInfoPage(chatId, messageId);
        }
        if (Callback.THIRD.equals(callbackQuery.getData())) {
            telegramUser.setState(UserStates.CAN_SEND_MESSAGES);
            telegramUserService.save(telegramUser);
            return responseHelper.createHelpPage(chatId, messageId);
        }
        if (Callback.DENIED.equals(callbackQuery.getData())) {
            telegramUser.setState(UserStates.CAN_SEND_MESSAGES);
            telegramUserService.save(telegramUser);
            return responseHelper.createDeniedMessage(chatId);
        }
        if (Callback.SUCCESS.equals(callbackQuery.getData())) {
            telegramUser.setState(UserStates.CAN_SEND_MESSAGES);
            telegramUserService.save(telegramUser);
            return responseHelper.createSuccessMessage(chatId);
        }
        if (Callback.BACK.equals(callbackQuery.getData())) {
            telegramUser.setState(UserStates.IN_PROGRESS);
            telegramUserService.save(telegramUser);
            return responseHelper.recreateMainMenu(chatId, messageId);
        } else {
            return responseHelper.createUnknownCallbackResponse(callbackQuery);
        }
    }

    private TelegramUser getSuitableTelegramUser(CallbackQuery callbackQuery, Message message) {
        if (callbackQuery.getData().equals(Callback.SUCCESS) || callbackQuery.getData().equals(Callback.DENIED)) {
            TelegramUser telegramUser;
            Long telegramUserIdFromComment = getTelegramUserIdFromComment(message);
            if (telegramUserIdFromComment == null) {
                throw new IllegalStateException("Wrong channel post");
            }
            telegramUser = telegramUserService.findBy(telegramUserIdFromComment);
            if (telegramUser == null) {
                throw new IllegalStateException("Telegram User doesn't exist");
            }
            return telegramUser;
        } else {
            return telegramUserService.findBy(callbackQuery.getFrom().getId());
        }
    }
}
