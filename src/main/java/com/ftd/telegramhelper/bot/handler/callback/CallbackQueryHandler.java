package com.ftd.telegramhelper.bot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {
    PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery);
}
