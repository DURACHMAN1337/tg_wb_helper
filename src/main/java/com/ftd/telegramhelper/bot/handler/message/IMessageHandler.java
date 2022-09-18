package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface IMessageHandler {
    BotApiMethod<?> processMessage(Message message) throws TelegramApiException, IncorrectFeedbackChannelPostException;
}
