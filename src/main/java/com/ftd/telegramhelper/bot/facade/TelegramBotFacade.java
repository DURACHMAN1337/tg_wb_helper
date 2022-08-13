package com.ftd.telegramhelper.bot.facade;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramBotFacade {
    PartialBotApiMethod<?> processUpdate(Update update);
}
