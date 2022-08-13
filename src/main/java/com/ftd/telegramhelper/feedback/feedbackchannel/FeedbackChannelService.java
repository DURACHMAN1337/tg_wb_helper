package com.ftd.telegramhelper.feedback.feedbackchannel;

import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.request.RequestHelper;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackChannelService {

    private final TelegramUserService telegramUserService;
    private final RequestHelper requestHelper;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelRepository feedbackChannelRepository;

    @Autowired
    public FeedbackChannelService(
            TelegramUserService telegramUserService,
            RequestHelper requestHelper,
            ResponseHelper responseHelper,
            ResponseHelper responseHelper1, FeedbackChannelRepository feedbackChannelRepository
    ) {
        this.telegramUserService = telegramUserService;
        this.requestHelper = requestHelper;
        this.responseHelper = responseHelper1;
        this.feedbackChannelRepository = feedbackChannelRepository;
    }

    public void setActualFeedbackChannel(String channelId) {
        FeedbackChannel existingChannel = findActualChannel();
        if (existingChannel != null) {
            updateFeedbackChannel(existingChannel, channelId);
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
        return requestHelper.getUpdates(getFeedbackChannelChatId());
    }

    // TODO: test this
    public void updateFeedback(String replyToMessageId, String message) throws TelegramApiException {
        responseHelper.replyToMessage(getFeedbackChannelChatId(), replyToMessageId, message);
    }

    // TODO: test this
    public Message createFeedback(TelegramUser forUser) throws TelegramApiException {
        Message feedbackMessage = responseHelper.sendMessage(
                getFeedbackChannelChatId(),
                forUser.getTelegramId().toString()
        );
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

    private String getFeedbackChannelChatId() throws IllegalStateException {
        return getActualFeedbackChannel().getChannelChatId();
    }

    private void updateFeedbackChannel(FeedbackChannel feedbackChannel, String channelId) {
        feedbackChannel.setChannelChatId(channelId);
        feedbackChannelRepository.save(feedbackChannel);
    }

    private void createFeedbackChannel(String channelId) {
        FeedbackChannel newChannel = new FeedbackChannel();
        newChannel.setChannelChatId(channelId);
        feedbackChannelRepository.save(newChannel);
    }
}
