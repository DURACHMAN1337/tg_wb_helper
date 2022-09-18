package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ICallbackQueryHandler {
    PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery)
            throws TelegramApiException, TelegramUserNotExistException, IncorrectFeedbackChannelPostException;
}
