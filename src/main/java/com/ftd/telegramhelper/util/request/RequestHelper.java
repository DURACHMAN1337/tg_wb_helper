package com.ftd.telegramhelper.util.request;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

import static com.ftd.telegramhelper.util.message.MessageUtils.isImage;

@Component
public class RequestHelper {

    private final ApplicationContext applicationContext;

    @Autowired
    public RequestHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public File getPhotoFrom(Message message) {
        final LongPollingBot bot = applicationContext.getBean(LongPollingBot.class);
        final String fileId;

        if (message.hasPhoto()) {
            List<PhotoSize> photoSizes = message.getPhoto();
            PhotoSize photoSize = photoSizes.get(photoSizes.size() - 1);
            fileId = photoSize.getFileId();

        } else if (message.hasDocument() && isImage(message.getDocument())) {
            fileId = message.getDocument().getFileId();

        } else {
            return null;
        }

        try {
            final String filePath = bot.execute(new GetFile(fileId)).getFilePath();
            return filePath != null ? bot.downloadFile(filePath) : null;
        } catch (TelegramApiException e) {
            return null;
        }
    }
}
