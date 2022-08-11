package com.ftd.tg_wb_helper.bot.facade;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramFacade {
    PartialBotApiMethod<?> processUpdate(Update update);
}
