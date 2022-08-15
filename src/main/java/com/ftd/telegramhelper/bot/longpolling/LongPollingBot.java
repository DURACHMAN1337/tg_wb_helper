package com.ftd.telegramhelper.bot.longpolling;

import com.ftd.telegramhelper.bot.facade.TelegramBotFacade;
import com.ftd.telegramhelper.config.bot.longpolling.LongPollingTelegramBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class LongPollingBot extends TelegramLongPollingBot {

    private final LongPollingTelegramBotConfig config;
    private final TelegramBotFacade facade;
    private final Logger logger = LoggerFactory.getLogger(LongPollingBot.class);

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
        PartialBotApiMethod<?> processedUpdate = facade.processUpdate(update);
        try {
            if (processedUpdate instanceof SendMessage) {
                execute(((SendMessage) processedUpdate));
            } else if (processedUpdate instanceof EditMessageText) {
                execute(((EditMessageText) processedUpdate));
            }
        } catch (TelegramApiException e) {
            logger.error("Something goes wrong");
        }
    }
}
