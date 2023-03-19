package com.ftd.telegramhelper.massmailing;

import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.response.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MassMailingServiceImpl implements MassMailingService {

    private static final Logger log = LoggerFactory.getLogger(MassMailingServiceImpl.class);
    final TelegramUserService telegramUserService;
    final ResponseHelper responseHelper;

    public MassMailingServiceImpl(TelegramUserService telegramUserService, ResponseHelper responseHelper) {
        this.telegramUserService = telegramUserService;
        this.responseHelper = responseHelper;
    }

    @Override
    public void sendMassMail(String message) {
        List<TelegramUser> users = telegramUserService.findAll();
        doSend(users, message);
    }

    @Override
    public void sendMassMail(String message, File photo) {
        List<TelegramUser> users = telegramUserService.findAll();
        doSend(users, message, photo);
    }

    private void doSend(List<TelegramUser> users, String message) {
        users.stream()
                .limit(30)
                .forEach(telegramUser -> sendMessageToUser(message, telegramUser));

        List<TelegramUser> nextBatch = users.stream().skip(30).toList();

        if (nextBatch.isEmpty()) {
            return;
        }

        try {
            TimeUnit.SECONDS.sleep(1);
            doSend(nextBatch, message);
        } catch (InterruptedException e) {
            log.warn("InterruptedException when send message", e);
        }
    }

    private void doSend(List<TelegramUser> users, String message, File photo) {
        users.stream()
                .limit(30)
                .forEach(telegramUser -> sendPhotoToUser(message, photo, telegramUser));

        List<TelegramUser> nextBatch = users.stream().skip(30).toList();

        if (nextBatch.isEmpty()) {
            return;
        }

        try {
            TimeUnit.SECONDS.sleep(1);
            doSend(nextBatch, message, photo);
        } catch (InterruptedException e) {
            log.warn("InterruptedException when send message", e);
        }
    }

    private boolean sendMessageToUser(String message, TelegramUser telegramUser) {
        try {
            String chatId = telegramUser.getChatId().toString();
            if (StringUtils.hasText(chatId)) {
                responseHelper.sendMessageAsync(chatId, message);
                return true;
            } else {
                log.warn("chat id not found for user " + telegramUser);
                return false;
            }
        } catch (TelegramApiException e) {
            log.warn("Exception when send message", e);
        }
        return false;
    }

    private boolean sendPhotoToUser(String message, File photo, TelegramUser telegramUser) {
        try {
            String chatId = telegramUser.getChatId().toString();
            if (StringUtils.hasText(chatId)) {
                responseHelper.sendPhotoAsync(chatId, message, photo);
                return true;
            } else {
                log.warn("chat id not found for user " + telegramUser);
                return false;
            }
        } catch (TelegramApiException e) {
            log.warn("Exception when send photo", e);
        }
        return false;
    }
}
