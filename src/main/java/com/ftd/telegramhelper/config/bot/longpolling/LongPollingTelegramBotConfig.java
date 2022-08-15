package com.ftd.telegramhelper.config.bot.longpolling;

import com.ftd.telegramhelper.config.bot.TelegramBotConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class LongPollingTelegramBotConfig implements TelegramBotConfig {

    @Value("${telegram-bot.userName}")
    private String userName;

    @Value("${telegram-bot.botToken}")
    private String botToken;

    @Value("${telegram-bot.admin.username}")
    private String botAdminUsername;

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotAdminUsername() {
        return botAdminUsername;
    }
}
