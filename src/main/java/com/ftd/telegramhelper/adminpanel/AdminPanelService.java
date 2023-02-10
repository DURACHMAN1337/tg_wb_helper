package com.ftd.telegramhelper.adminpanel;

import com.ftd.telegramhelper.config.bot.longpolling.LongPollingTelegramBotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;

@Service
public class AdminPanelService {

    private final LongPollingTelegramBotConfig botConfig;

    @Autowired
    public AdminPanelService(LongPollingTelegramBotConfig longPollingTelegramBotConfig) {
        this.botConfig = longPollingTelegramBotConfig;
    }

    public boolean checkPassword(@Nullable String password) {
        if (StringUtils.hasText(password)) {
            return password.equals(botConfig.getAdminPanelPassword());
        } else {
            return false;
        }
    }
}
