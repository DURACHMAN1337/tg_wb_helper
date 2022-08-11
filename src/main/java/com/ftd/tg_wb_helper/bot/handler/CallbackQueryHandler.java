package com.ftd.tg_wb_helper.bot.handler;

import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {
    PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser);
}
