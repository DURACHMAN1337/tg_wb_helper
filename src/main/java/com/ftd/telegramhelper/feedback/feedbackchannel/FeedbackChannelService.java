package com.ftd.telegramhelper.feedback.feedbackchannel;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Service
public class FeedbackChannelService {

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;


    @Autowired
    public FeedbackChannelService(
            TelegramUserService telegramUserService,
            ResponseHelper responseHelper,
            FeedbackChannelConfig feedbackChannelConfig
    ) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
    }

    public void updateFeedback(String replyToMessageId, String message) throws TelegramApiException {
        responseHelper.replyToMessage(feedbackChannelConfig.getChannelChatId(), replyToMessageId, message);
    }

    public void updateFeedback(String replyMessageId, File photoOrDocument, boolean isDocument) throws TelegramApiException {
        responseHelper.replyToMessage(
                feedbackChannelConfig.getChannelChatId(),
                replyMessageId,
                photoOrDocument,
                isDocument,
                responseHelper.createAcceptButton(),
                responseHelper.createRejectButton()
        );
    }

    public Message createFeedback(TelegramUser forUser) throws TelegramApiException {
        Message feedbackMessage = responseHelper.sendMessage(
                feedbackChannelConfig.getChannelId(),
                getFeedbackPostTitle(forUser)
        );
        telegramUserService.save(forUser);
        return feedbackMessage;
    }

    private String getFeedbackPostTitle(TelegramUser forUser) {
        return forUser.getTelegramId() +
                "\n" +
                "Пользователь: " +
                (forUser.getFirstName() == null ? "Empty name" : forUser.getFirstName()) + " " +
                (forUser.getLastName() == null ? "" : forUser.getLastName()) +
                "\n" +
                "@" + forUser.getUsername();
    }


}
