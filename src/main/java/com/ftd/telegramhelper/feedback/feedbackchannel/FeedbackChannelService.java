package com.ftd.telegramhelper.feedback.feedbackchannel;

import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.rest.TelegramRestApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackChannelService {

    private final TelegramUserService telegramUserService;
    private final TelegramRestApiUtils telegramRestApiUtils;
    private final FeedbackChannelRepository feedbackChannelRepository;

    @Autowired
    public FeedbackChannelService(
            TelegramUserService telegramUserService,
            TelegramRestApiUtils telegramRestApiUtils,
            FeedbackChannelRepository feedbackChannelRepository
    ) {
        this.telegramUserService = telegramUserService;
        this.telegramRestApiUtils = telegramRestApiUtils;
        this.feedbackChannelRepository = feedbackChannelRepository;
    }

    public void setActualFeedbackChannel(String channelId) {
        FeedbackChannel existingChannel = findActualChannel();
        if (existingChannel != null) {
            updateFeedbackChannelChannel(existingChannel, channelId);
        } else {
            createFeedbackChannel(channelId);
        }
    }

    @NotNull
    public FeedbackChannel getActualFeedbackChannel() throws IllegalStateException {
        return Optional.ofNullable(findActualChannel())
                .orElseThrow(() -> new IllegalStateException("Feedback channel not configured yet"));
    }

    // TODO: test this
    public List<Update> getChannelUpdates() {
        return telegramRestApiUtils.getFeedbackChannelUpdates();
    }

    // TODO: test this
    public void updateFeedback(String replyToMessageId, String message) {
        telegramRestApiUtils.replyToFeedback(replyToMessageId, message);
    }

    // TODO: test this
    public Message createFeedback(TelegramUser forUser) {
        Message feedbackMessage = telegramRestApiUtils.createFeedback(forUser.getTelegramId().toString());
        forUser.setFeedbackMessageId(feedbackMessage.getMessageId().toString());
        telegramUserService.save(forUser);

        return feedbackMessage;
    }

    @Nullable
    private FeedbackChannel findActualChannel() {
        return feedbackChannelRepository
                .findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }

    private void updateFeedbackChannelChannel(FeedbackChannel feedbackChannel, String channelId) {
        feedbackChannel.setChannelChatId(channelId);
        feedbackChannelRepository.save(feedbackChannel);
    }

    private void createFeedbackChannel(String channelId) {
        FeedbackChannel newChannel = new FeedbackChannel();
        newChannel.setChannelChatId(channelId);
        feedbackChannelRepository.save(newChannel);
    }
}
