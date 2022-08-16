package com.ftd.telegramhelper.util.message;

import org.telegram.telegrambots.meta.api.objects.Message;


public class MessageService {

    public static Long getTelegramUserIdFromComment(Message message) {
        Message originalMessage = message.getReplyToMessage();
        String[] split = originalMessage.getText().split("\n");
        if (split.length == 3) {
            String telegramUserId = split[0];
            return Long.valueOf(telegramUserId);
        } else {
            return null;
        }
    }

}
