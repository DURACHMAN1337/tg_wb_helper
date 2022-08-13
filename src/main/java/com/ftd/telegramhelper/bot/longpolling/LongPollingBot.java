package com.ftd.telegramhelper.bot.longpolling;

import com.ftd.telegramhelper.bot.facade.TelegramBotFacade;
import com.ftd.telegramhelper.config.longpolling.LongPollingTelegramBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LongPollingBot extends TelegramLongPollingBot {

    private final LongPollingTelegramBotConfig config;
    private final TelegramBotFacade facade;

    @Autowired
    public LongPollingBot(LongPollingTelegramBotConfig config, TelegramBotFacade facade) {
        this.config = config;
        this.facade = facade;
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        facade.processUpdate(update);
    }
}
