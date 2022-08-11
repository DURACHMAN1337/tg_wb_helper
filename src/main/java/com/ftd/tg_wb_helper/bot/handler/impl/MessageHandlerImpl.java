package com.ftd.tg_wb_helper.bot.handler.impl;

import com.ftd.tg_wb_helper.bot.handler.MessageHandler;
import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import com.ftd.tg_wb_helper.model.enums.Command;
import com.ftd.tg_wb_helper.service.TelegramUserService;
import com.ftd.tg_wb_helper.util.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
public class MessageHandlerImpl implements MessageHandler {

    @Autowired
    private TelegramUserService telegramUserService;
    @Autowired
    private ResponseHelper responseHelper;

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

    private void createTelegramUserIfNotExist(User telegramUser, Long chatId) {
        TelegramUser existingUser = telegramUserService.findBy(telegramUser.getId());
        if (existingUser == null) {
            telegramUserService.create(telegramUser.getId(),
                    telegramUser.getFirstName(),
                    telegramUser.getLastName(),
                    telegramUser.getUserName(),
                    chatId);
        }
    }
}
