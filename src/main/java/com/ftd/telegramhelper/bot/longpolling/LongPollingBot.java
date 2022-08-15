package com.ftd.telegramhelper.bot.longpolling;

import com.ftd.telegramhelper.bot.facade.TelegramBotFacade;
import com.ftd.telegramhelper.config.bot.longpolling.LongPollingTelegramBotConfig;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.feedback.feedbackchannel.FeedbackChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class LongPollingBot extends TelegramLongPollingBot {

    private final LongPollingTelegramBotConfig config;
    private final TelegramBotFacade facade;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private FeedbackChannelService feedbackChannelService;


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
        String chatId1 = "@test1337322";
        feedbackChannelService.setActualFeedbackChannel(chatId1);
        feedbackChannelService.getChannelUpdates();
        facade.processUpdate(update);
    }
}
