package com.ftd.tg_wb_helper.bot;

import com.ftd.tg_wb_helper.bot.facade.TelegramFacadeImpl;
import com.ftd.tg_wb_helper.config.WbTelegramBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class WBTelegramBot extends TelegramWebhookBot {


    @Autowired
    private WbTelegramBotConfig telegramBotConfig;
    @Autowired
    private TelegramFacadeImpl telegramFacade;


    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUserName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return (BotApiMethod<?>) telegramFacade.processUpdate(update);
    }

    @Override
    public String getBotPath() {
        return telegramBotConfig.getWebHookPath();
    }
}
