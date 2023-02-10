package com.ftd.telegramhelper.config.bot.longpolling;

import com.ftd.telegramhelper.config.bot.TelegramBotConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nullable;

@Configuration
@PropertySource("classpath:application.properties")
public class LongPollingTelegramBotConfig implements TelegramBotConfig {

    @Value("${telegram-bot.userName}")
    private String userName;

    @Value("${telegram-bot.botToken}")
    private String botToken;

    @Value("${telegram-bot.admin.username}")
    private String botAdminUsername;

    @Value("${telegram-bot.admin.panel.password}")
    private String adminPanelPassword;

    private String customPassword;

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

    @Override
    public String getAdminPanelPassword() {
        return StringUtils.hasText(customPassword) ? customPassword : adminPanelPassword;
    }

    @Override
    public void setAdminPanelCustomPassword(String password) {
        customPassword = password;
    }

    @Override
    public boolean isMainAdmin(@Nullable User user) {
        if (user == null) {
            return false;
        }

        String username = user.getUserName();
        return StringUtils.hasText(username) && username.equals(getBotAdminUsername());
    }
}
