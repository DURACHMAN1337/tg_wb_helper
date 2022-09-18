package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.bot.meta.callback.Callback;
import com.ftd.telegramhelper.bot.meta.callback.ICallback;
import com.ftd.telegramhelper.bot.meta.value.callback.CallbackValue;
import com.ftd.telegramhelper.bot.service.callback.CallbackService;
import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallbackQueryHandler implements ICallbackQueryHandler {

    private final FeedbackService feedbackService;
    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final CallbackService callbackService;

    @Autowired
    public CallbackQueryHandler(
            FeedbackService feedbackService,
            ResponseHelper responseHelper,
            TelegramUserService telegramUserService,
            CallbackService callbackService) {
        this.feedbackService = feedbackService;
        this.responseHelper = responseHelper;
        this.telegramUserService = telegramUserService;
        this.callbackService = callbackService;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = String.valueOf(callbackQuery.getMessage().getChatId());
        ICallback callback = Callback.create(CallbackValue.create(callbackQuery.getData()));
        return doProcessCallbackQuery(chatId, messageId, callback);
    }

    private PartialBotApiMethod<?> doProcessCallbackQuery(
            String chatId,
            Integer messageId,
            ICallback callback
    ) throws TelegramApiException {
        if (callbackService.isKnownCallback(callback)) {
            // add code here
            return null;
        } else {
            responseHelper.handleError(chatId);
            return null;
        }
    }

    private void createFeedbackIfNeeded(TelegramUser telegramUser) throws TelegramApiException {
        if (!StringUtils.hasText(telegramUser.getFeedbackMessageId())) {
            feedbackService.createFeedback(telegramUser);
        }
    }

    private void setSuitableState(UserStates state, TelegramUser forUser) {
        forUser.setState(state);
        telegramUserService.save(forUser);
    }
}
