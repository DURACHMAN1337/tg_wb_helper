package com.ftd.telegramhelper.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    public void setFeedbackChannel(String channelId) {
        feedbackService.setFeedbackChannel(channelId);
    }
}
