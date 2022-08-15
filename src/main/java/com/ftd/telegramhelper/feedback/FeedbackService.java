package com.ftd.telegramhelper.feedback;

import com.ftd.telegramhelper.feedback.feedbackchannel.FeedbackChannelService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;

@Service
public class FeedbackService {

    private final FeedbackChannelService feedbackChannelService;

    @Autowired
    public FeedbackService(FeedbackChannelService feedbackChannelService) {
        this.feedbackChannelService = feedbackChannelService;
    }

    public void updateFeedback(TelegramUser forUser, @Nullable String message) throws TelegramApiException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        boolean feedbackAlreadyExist = StringUtils.hasText(feedbackMessageId);
        if (feedbackAlreadyExist && message != null) {
            feedbackChannelService.updateFeedback(feedbackMessageId, message);
        } else if (!feedbackAlreadyExist) {
            Message feedbackMessage = feedbackChannelService.createFeedback(forUser);
            feedbackMessageId = String.valueOf(feedbackMessage.getMessageId());
            if (message != null) {
                feedbackChannelService.updateFeedback(feedbackMessageId, message);
            }
        }
    }
}
