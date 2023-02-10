package com.ftd.telegramhelper.config.bot;

import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.Nullable;

public interface TelegramBotConfig {
    String getUsername();

    String getBotToken();

    String getBotAdminUsername();

    /**
     * null -> false
     */
    boolean isMainAdmin(@Nullable User user);

    String getAdminPanelPassword();

    void setAdminPanelCustomPassword(String password);
}
