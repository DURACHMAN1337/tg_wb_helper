package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;

    @Autowired
    public MessageHandlerImpl(TelegramUserService telegramUserService, ResponseHelper responseHelper, FeedbackChannelConfig feedbackChannelConfig) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
    }

    @Override
    public BotApiMethod<?> processMessage(Message message) {
        Long chatId = message.getChatId();
        String command = message.getText();
        User user = message.getFrom();

        if (Command.START.getValue().equals(command)) {
            createTelegramUserIfNotExist(user, chatId);
            return responseHelper.createMainMenu(chatId.toString());
        }
        TelegramUser telegramUser = telegramUserService.findBy(user.getId());
        if (telegramUser != null && telegramUser.getState().equals(UserStates.CAN_SEND_MESSAGES)) {
            if (message.hasPhoto()) {
                // TODO: 16.08.2022 отправить фото в чат ChatChannelId на сообщение replyMessageId
            } else if (message.hasText()) {
                try {
                    responseHelper.replyToUserMessage(feedbackChannelConfig.getChannelChatId(),
                            message.getText(), telegramUser);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        if (message.getReplyToMessage() != null) {
            Message originalMessage = message.getReplyToMessage();
            String[] split = originalMessage.getText().split("\n");
            if (split.length == 3) {
                String telegramUserId = split[0];
                TelegramUser existingUser = telegramUserService.findBy(Long.valueOf(telegramUserId));
                if (existingUser != null) {
                    try {
                        responseHelper.sendMessage(String.valueOf(existingUser.getChatId()), message.getText());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private void createTelegramUserIfNotExist(User user, Long chatId) {
        TelegramUser existingUser = telegramUserService.findBy(user.getId());
        if (existingUser == null) {
            telegramUserService.createAndSaveFrom(user, chatId);
        }
    }
}
