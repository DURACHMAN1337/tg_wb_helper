package com.ftd.telegramhelper.bot.handler.message;

import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class MessageHandlerImpl implements MessageHandler {

    private final TelegramUserService telegramUserService;
    private final ResponseHelper responseHelper;

    @Autowired
    public MessageHandlerImpl(TelegramUserService telegramUserService, ResponseHelper responseHelper) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
    }

    @Override
    public BotApiMethod<?> processMessage(Message message) {
        Long chatId = message.getChatId();
        String command = message.getText();
        User user = message.getFrom();

        if (Command.START.getValue().equals(command)) {
            createTelegramUserIfNotExist(user,chatId);
            return responseHelper.createMainMenu(chatId);
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
