package com.ftd.telegramhelper.bot.webhook.controller;

import com.ftd.telegramhelper.bot.webhook.WebHookTelegramBot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Do not uncomment, use only LongPolling bot type. May be need to delete this class.
 */
@Deprecated
//@RestController
public class WebHookController {

    //@Autowired
    private WebHookTelegramBot telegramBot;

    //@PostMapping("/")
    public BotApiMethod<?> OnUpdateReceived(@RequestBody Update update){
        return telegramBot.onWebhookUpdateReceived(update);
    }

    //@GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok().build();
    }
}
