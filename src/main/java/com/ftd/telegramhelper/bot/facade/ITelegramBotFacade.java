package com.ftd.telegramhelper.bot.facade;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ITelegramBotFacade {
    PartialBotApiMethod<?> processUpdate(Update update)
            throws TelegramApiException, TelegramUserNotExistException, IncorrectFeedbackChannelPostException;
}
