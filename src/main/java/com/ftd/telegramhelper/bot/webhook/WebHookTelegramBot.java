package com.ftd.telegramhelper.bot.webhook;

import com.ftd.telegramhelper.bot.facade.TelegramBotFacadeImpl;
import com.ftd.telegramhelper.config.webhook.WebHookTelegramBotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


/**
 * Do not uncomment, use only LongPolling bot type. May be need to delete this class.
 */
@Deprecated
//@Component
public class WebHookTelegramBot extends TelegramWebhookBot {


    //@Autowired
    private WebHookTelegramBotConfig telegramBotConfig;
    //@Autowired
    private TelegramBotFacadeImpl telegramFacade;


    @Override
    public String getBotUsername() {
        return telegramBotConfig.getUsername();
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
