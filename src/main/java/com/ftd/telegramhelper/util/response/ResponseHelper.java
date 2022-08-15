package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
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
    private MessageBundle messageBundle;

    @Autowired
    public ResponseHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SendMessage createMainMenu(String chatId) {
        SendMessage message = InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), messageBundle.loadMessage("ftd.telegram_helper.message.welcome"))
                .row()
                .button("1️⃣", "1")
                .button("2️⃣", "2")
                .button("3️⃣", "3")
                .endRow()
                .buildAsSendMessage();
        try {
            applicationContext.getBean(LongPollingBot.class).execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return message;
    }

    public SendMessage createInfoPage(String chatId) {
        SendMessage message = InlineKeyboardMarkupBuilder.create(
                        chatId,
                        messageBundle.loadMessage("ftd.telegram_helper.message.info")
                )
                .row()
                .button("Назад", "back")
                .endRow()
                .buildAsSendMessage();
        return message;
    }

    public SendMessage createHelpPage(String chatId){
        SendMessage message = InlineKeyboardMarkupBuilder.create(
                        chatId,
                        messageBundle.loadMessage("ftd.telegram_helper.message.help")
                )
                .row()
                .button("Назад", "back")
                .endRow()
                .buildAsSendMessage();
        return message;
    }

    public SendMessage createPostForChannel(String channelChatId, TelegramUser telegramUser) {
        SendMessage message = InlineKeyboardMarkupBuilder.create(channelChatId)
                .buildAsSendMessage();
        StringBuilder sb = new StringBuilder();
        sb.append(telegramUser.getTelegramId())
                .append("\n")
                .append(telegramUser.getFirstName())
                .append(" ")
                .append(telegramUser.getLastName())
                .append("\n")
                .append(telegramUser.getUsername());
        message.setText(sb.toString());
        message.setChatId(channelChatId);
        return message;
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
