package com.ftd.telegramhelper.feedback.feedbackchannel;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    // TODO: test this
    public void updateFeedback(String replyToMessageId, String message) throws TelegramApiException {
        responseHelper.replyToMessage(getFeedbackChannelId(), replyToMessageId, message);
    }

    public Message createFeedback(TelegramUser forUser) throws TelegramApiException {
        Message feedbackMessage = responseHelper.sendMessage(
                getFeedbackChannelId(),
                getFeedbackPostTitle(forUser)
        );
        forUser.setFeedbackMessageId(feedbackMessage.getMessageId().toString());
        telegramUserService.save(forUser);

        return feedbackMessage;
    }

    private String getFeedbackPostTitle(TelegramUser forUser) {
        return forUser.getTelegramId() + "\n" +                                 // id [row 1]
                forUser.getFirstName() + " " + forUser.getLastName() +  // hummable name [row 2]
                "\n" + forUser.getUsername();                           // telegram login [row 3]
    }

    private String getFeedbackChannelId() throws IllegalStateException {
        return feedbackChannelConfig.getChannelId();
    }
}
