package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    private final FeedbackService feedbackService;
    private final ResponseHelper responseHelper;

    @Autowired
    public CallbackQueryHandlerImpl(FeedbackService feedbackService, ResponseHelper responseHelper) {
        this.feedbackService = feedbackService;
        this.responseHelper = responseHelper;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser) {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = String.valueOf(telegramUser.getChatId());
        if (Callback.FIRST.equals(callbackQuery.getData())) {
            try {
                feedbackService.updateFeedback(telegramUser, null);
                return responseHelper.createInstructionMessage(chatId, messageId);
            } catch (Exception e) {
                return responseHelper.createErrorResponse(callbackQuery);
            }
        } else if (Callback.SECOND.equals(callbackQuery.getData())) {
            return responseHelper.createInfoPage(chatId, messageId);
        }
        if (Callback.THIRD.equals(callbackQuery.getData())) {
            return responseHelper.createHelpPage(chatId, messageId);
        }
        if (Callback.BACK.equals(callbackQuery.getData())) {
            return responseHelper.recreateMainMenu(chatId, messageId);
        } else {
            return responseHelper.createUnknownCallbackResponse(callbackQuery);
        }
    }
}
