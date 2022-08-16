package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.config.bot.feedbackchanner.FeedbackChannelConfig;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.message.MessageService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;
    private final FeedbackChannelConfig feedbackChannelConfig;
    private final WebApplicationContext webApplicationContext;
    private final FeedbackService feedbackService;

    @Autowired
    public MessageHandlerImpl(TelegramUserService telegramUserService, ResponseHelper responseHelper, FeedbackChannelConfig feedbackChannelConfig, WebApplicationContext webApplicationContext, FeedbackService feedbackService) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
        this.feedbackChannelConfig = feedbackChannelConfig;
        this.webApplicationContext = webApplicationContext;
        this.feedbackService = feedbackService;
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
                LongPollingBot botBean = webApplicationContext.getBean(LongPollingBot.class);
                List<PhotoSize> list = message.getPhoto();
                PhotoSize photoSize = list.get(list.size() - 1);
                try {
                    GetFile getFile = new GetFile();
                    getFile.setFileId(photoSize.getFileId());
                    String filePath = botBean.execute(getFile).getFilePath();
                    File downloadFile = botBean.downloadFile(filePath);
                    botBean.execute(
                            SendPhoto.builder()
                                    .chatId(feedbackChannelConfig.getChannelChatId())
                                    .replyToMessageId(Integer.valueOf(telegramUser.getFeedbackMessageId()))
                                    .photo(new InputFile().setMedia(downloadFile))
                                    .build()
                    );
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

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
            Long telegramUserIdFromComment = MessageService.getTelegramUserIdFromComment(message);
            if (telegramUserIdFromComment == null) {
                throw new IllegalStateException("Wrong channel post");
            }
            TelegramUser existingUser = telegramUserService.findBy(telegramUserIdFromComment);
            if (existingUser != null) {
                try {
                    responseHelper.sendMessage(String.valueOf(existingUser.getChatId()), message.getText());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
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
