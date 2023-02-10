package com.ftd.telegramhelper.massmailing;

import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MassMailingServiceImpl implements MassMailingService {

    final TelegramUserService telegramUserService;
    final ResponseHelper responseHelper;

    public MassMailingServiceImpl(TelegramUserService telegramUserService, ResponseHelper responseHelper) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
    }

    @Override
    public void sendMassMail(String message) {
        telegramUserService.findAll().forEach(telegramUser -> sendMessageToUser(message, telegramUser));
    }

    public void sendMessageToUser(String message, TelegramUser telegramUser) {
        try {
            responseHelper.sendMessage(String.valueOf(telegramUser.getChatId()), message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
