package com.ftd.telegramhelper.feedback;

import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.feedback.feedbackchannel.FeedbackChannelService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;

@Service
public class FeedbackService {

    private final FeedbackChannelService feedbackChannelService;

    @Autowired
    public FeedbackService(FeedbackChannelService feedbackChannelService) {
        this.feedbackChannelService = feedbackChannelService;
    }

    public void updateFeedback(@NotNull TelegramUser forUser, @Nullable String message) throws TelegramApiException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        boolean feedbackAlreadyExist = StringUtils.hasText(feedbackMessageId);
        if (feedbackAlreadyExist && StringUtils.hasText(message)) {
            feedbackChannelService.updateFeedback(feedbackMessageId, message);
        } else if (!feedbackAlreadyExist) {
            Message feedbackMessage = feedbackChannelService.createFeedback(forUser);
            feedbackMessageId = String.valueOf(feedbackMessage.getMessageId());
            if (StringUtils.hasText(message)) {
                feedbackChannelService.updateFeedback(feedbackMessageId, message);
            }
        }
    }

    public void updateFeedback(
            @NotNull TelegramUser forUser,
            @NotNull File photoOrDocument,
            boolean isDocument
    ) throws TelegramApiException, IncorrectFeedbackChannelPostException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        boolean feedbackAlreadyExist = StringUtils.hasText(feedbackMessageId);
        if (feedbackAlreadyExist && photoOrDocument != null) {
            feedbackChannelService.updateFeedback(feedbackMessageId, photoOrDocument, isDocument);
        } else if (!feedbackAlreadyExist) {
            throw new IncorrectFeedbackChannelPostException();
        }
    }
}
