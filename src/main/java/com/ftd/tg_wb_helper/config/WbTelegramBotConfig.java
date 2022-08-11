package com.ftd.tg_wb_helper.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class WbTelegramBotConfig {

    @Value("${telegram-bot.webHookPath}")
    private String webHookPath;

    @Value("${telegram-bot.userName}")
    private String userName;

    @Value("${telegram-bot.botToken}")
    private String botToken;

    public String getWebHookPath() {
        return webHookPath;
    }

    public String getUserName() {
        return userName;
    }

    public String getBotToken() {
        return botToken;
    }
}
