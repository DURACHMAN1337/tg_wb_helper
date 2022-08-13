package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ResponseHelper {

    private final ApplicationContext applicationContext;

    @Autowired
    public ResponseHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SendMessage createMainMenu(Long chatId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), "123")
                .row()
                .button("1", "1")
                .button("2", "2")
                .button("3", "3")
                .endRow()
                .buildAsSendMessage();
    }

    public Message sendMessage(String chatId, String message) throws TelegramApiException {
        return applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .build()
        );
    }

    public void replyToMessage(String chatId, String replyToMessageId, String message) throws TelegramApiException {
        applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .replyToMessageId(Integer.parseInt(replyToMessageId))
                        .text(message)
                        .build()
        );
    }
}
