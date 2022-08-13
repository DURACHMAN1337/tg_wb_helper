package com.ftd.telegramhelper.util.rest;

import com.ftd.telegramhelper.config.longpolling.LongPollingTelegramBotConfig;
import com.ftd.telegramhelper.feedback.feedbackchannel.FeedbackChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class TelegramRestApiUtils {

    private final ApplicationContext applicationContext;
    private final LongPollingTelegramBotConfig botConfig;
    private final RestTemplate restTemplate;

    private static final String GET_UPDATES_URL = "https://api.telegram.org/bot%s/getUpdates?chat_id=%s";
    private static final String SEND_MESSAGE_URL = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private static final String REPLY_TO_MESSAGE_URL = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&reply_to_message_id=%s&text=%s";

    @Autowired
    public TelegramRestApiUtils(
            ApplicationContext applicationContext,
            LongPollingTelegramBotConfig botConfig,
            RestTemplate restTemplate
    ) {
        this.applicationContext = applicationContext;
        this.botConfig = botConfig;
        this.restTemplate = restTemplate;
    }

    public List<Update> getFeedbackChannelUpdates() {
        return restTemplate.exchange(
                getFeedbackChannelUpdatesUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Update>>() {
                }
        ).getBody();
    }

    public Message createFeedback(String message) {
        return restTemplate.exchange(
                getFeedbackChannelSendMessageUrl(message),
                HttpMethod.GET,
                null,
                Message.class
        ).getBody();
    }

    public void replyToFeedback(String replyToMessageId, String message) {
        restTemplate.execute(
                getFeedbackChannelReplyToMessageUrl(replyToMessageId, message),
                HttpMethod.GET,
                null,
                null
        );
    }

    @NotNull
    private String getFeedbackChannelUpdatesUrl() throws IllegalStateException {
        return String.format(
                GET_UPDATES_URL,
                getToken(),
                getFeedbackChannelChatId()
        );
    }

    @NotNull
    private String getFeedbackChannelSendMessageUrl(String message) throws IllegalStateException {
        return String.format(
                SEND_MESSAGE_URL,
                getToken(),
                getFeedbackChannelChatId(),
                message
        );
    }

    @NotNull
    private String getFeedbackChannelReplyToMessageUrl(String replyToMessageId, String message) throws IllegalStateException {
        return String.format(
                REPLY_TO_MESSAGE_URL,
                getToken(),
                getFeedbackChannelChatId(),
                replyToMessageId,
                message
        );
    }

    @NotNull
    private String getFeedbackChannelChatId() throws IllegalStateException {
        return applicationContext.getBean(FeedbackChannelService.class)
                .getActualFeedbackChannel()
                .getChannelChatId();
    }

    @NotNull
    private String getToken() {
        return botConfig.getBotToken();
    }
}
