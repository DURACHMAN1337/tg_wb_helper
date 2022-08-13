package com.ftd.telegramhelper.config.webhook;


import com.ftd.telegramhelper.config.TelegramBotConfig;

//@Configuration
//@PropertySource("classpath:application.properties")
/**
 * Do not uncomment, use only LongPolling bot type. May be need to delete this class.
 */
@Deprecated
public class WebHookTelegramBotConfig implements TelegramBotConfig {

    //@Value("${telegram-bot.webHookPath}")
    private String webHookPath;

    //@Value("${telegram-bot.userName}")
    private String userName;

    //@Value("${telegram-bot.botToken}")
    private String botToken;

    //@Value("${telegram-bot.admin.username}")
    private String botAdminUsername;

    //@Value("${telegram-bot.admin.password}")
    private String botAdminPassword;

    public String getWebHookPath() {
        return webHookPath;
    }

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
    public String getBotAdminPassword() {
        return botAdminPassword;
    }
}
