package com.ftd.telegramhelper.util.message;

import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.Nullable;

public class MessageUtils {

    @Nullable
    public static Long getTelegramUserIdFromFeedbackPost(Message feedbackPostMessage) {
        Message replyToMessage = feedbackPostMessage.getReplyToMessage();
        if (replyToMessage == null) {
           replyToMessage = feedbackPostMessage; // when we create feedback post firstly
        }

        final String[] split = replyToMessage.getText().split("\n");
        if (split.length == 3) {
            return Long.valueOf(split[0]);
        }

        return null;
    }

    public static boolean isImage(Document document) {
        return MimeTypeUtils.parseMimeType(document.getMimeType()).getType().equals("image");
    }
}
