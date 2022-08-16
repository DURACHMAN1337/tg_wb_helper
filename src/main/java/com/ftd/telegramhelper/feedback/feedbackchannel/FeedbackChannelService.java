package com.ftd.telegramhelper.feedback.feedbackchannel;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class FeedbackChannelService {

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private final WebApplicationContext webApplicationContext;


    @Autowired
    public FeedbackChannelService(
            TelegramUserService telegramUserService,
            ResponseHelper responseHelper,
            FeedbackChannelConfig feedbackChannelConfig,
            WebApplicationContext webApplicationContext) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
        this.webApplicationContext = webApplicationContext;
    }

    // TODO: test this
    public void updateFeedback(String replyToMessageId, String message) throws TelegramApiException {
        responseHelper.replyToMessage(feedbackChannelConfig.getChannelId(), replyToMessageId, message);
    }

    public Message createFeedback(TelegramUser forUser) throws TelegramApiException {
        Message feedbackMessage = responseHelper.sendMessage(
                feedbackChannelConfig.getChannelId(),
                getFeedbackPostTitle(forUser)
        );
        forUser.setFeedbackMessageId(String.valueOf(getMessageIdForLastMessage(feedbackChannelConfig.getChannelChatId())));
        telegramUserService.save(forUser);

        return feedbackMessage;
    }

    public Integer getMessageIdForLastMessage(String chatId) {
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text("test")
                .build();

        try {
            LongPollingBot bean = webApplicationContext.getBean(LongPollingBot.class);
            Message execute = bean.execute(sendMessage);
            Integer messageId = execute.getMessageId();
            int res = messageId + 1;
            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .build();
            bean.execute(deleteMessage);
            return res;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFeedbackPostTitle(TelegramUser forUser) {
        return forUser.getTelegramId() + "\n" +
                "Пользователь: " + forUser.getFirstName() + " " + forUser.getLastName() +
                "\n" + "@" + forUser.getUsername();
    }


}
