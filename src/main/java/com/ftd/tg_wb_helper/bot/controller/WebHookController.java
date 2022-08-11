package com.ftd.tg_wb_helper.bot.controller;

import com.ftd.tg_wb_helper.bot.WBTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {

    @Autowired
    private com.ftd.tg_wb_helper.bot.WBTelegramBot telegramBot;

    @PostMapping("/")
    public BotApiMethod<?> OnUpdateReceived(@RequestBody Update update){
        return telegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping
    public ResponseEntity<?> get(){
        return ResponseEntity.ok().build();
    }
}
