package com.ftd.telegramhelper.feedback;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;

@Service
public class FeedbackService {

    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired
    public FeedbackService(ResponseHelper responseHelper, FeedbackChannelConfig feedbackChannelConfig) {
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
    }

    public void updateFeedback(@NotNull TelegramUser forUser, @Nullable String message) throws TelegramApiException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        boolean feedbackAlreadyExist = StringUtils.hasText(feedbackMessageId);
        if (feedbackAlreadyExist && StringUtils.hasText(message)) {
            responseHelper.replyToMessage(
                    feedbackChannelConfig.getChannelChatId(),
                    feedbackMessageId,
                    message
            );
            logger.info("Feedback has been updated for user " + forUser);
        } else if (!feedbackAlreadyExist) {
            throw new IllegalStateException("Feedback post not exist!");
        }
    }

    public void updateFeedback(
            @NotNull TelegramUser forUser,
            @NotNull File photoOrDocument,
            boolean isDocument
    ) throws TelegramApiException {
        String feedbackMessageId = forUser.getFeedbackMessageId();
        boolean feedbackAlreadyExist = StringUtils.hasText(feedbackMessageId);
        if (feedbackAlreadyExist && photoOrDocument != null) {
            responseHelper.replyToMessage(
                    feedbackChannelConfig.getChannelChatId(),
                    feedbackMessageId,
                    photoOrDocument,
                    isDocument,
                    responseHelper.createAcceptButton(),
                    responseHelper.createRejectButton()
            );
            logger.info("Feedback has been updated for user " + forUser);
        } else if (!feedbackAlreadyExist) {
            throw new IllegalStateException("Feedback post not exist!");
        }
    }

    public void createFeedback(TelegramUser forUser) throws TelegramApiException {
        responseHelper.sendMessage(
                feedbackChannelConfig.getChannelId(),
                getFeedbackPostTitle(forUser)
        );
        logger.info("Feedback has been created for user " + forUser);
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
