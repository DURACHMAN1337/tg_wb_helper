package com.ftd.tg_wb_helper.bot.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {
    BotApiMethod<?> processMessage(Message message);
}
