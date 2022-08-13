package com.ftd.telegramhelper.bot.handler.message;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {
    BotApiMethod<?> processMessage(Message message);
}
