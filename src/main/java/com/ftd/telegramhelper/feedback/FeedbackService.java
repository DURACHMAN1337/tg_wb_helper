package com.ftd.telegramhelper.feedback;

import com.ftd.telegramhelper.feedback.feedbackchannel.FeedbackChannelService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackChannelService feedbackChannelService;

    @Autowired
    public FeedbackService(FeedbackChannelService feedbackChannelService) {
        this.feedbackChannelService = feedbackChannelService;
    }

    public void setFeedbackChannel(String channelId) {
        feedbackChannelService.setActualFeedbackChannel(channelId);
    }

    // TODO: test this
    public void updateFeedback(TelegramUser forUser, String message) throws TelegramApiException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        if (StringUtils.hasText(feedbackMessageId)) {
            feedbackChannelService.updateFeedback(feedbackMessageId, message);
        } else {
            Message feedbackMessage = feedbackChannelService.createFeedback(forUser);
            feedbackMessageId = feedbackMessage.getMessageId().toString();
            feedbackChannelService.updateFeedback(feedbackMessageId, message);
        }
    }

    public List<Update> getUpdates(TelegramUser forUser) {
        return feedbackChannelService
                .getChannelUpdates()
                .stream()
                .filter(update -> { // TODO: create filter to find right updates for current user
                    return true; // maybe: update.getMessage().getReplyToMessage().getText().equals(forUser.getTelegramId());
                })
                .collect(Collectors.toList());
    }
}
