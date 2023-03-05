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
        List<TelegramUser> successfullySent = users.stream()
                .filter(telegramUser -> sendMessageToUser(message, telegramUser))
                .toList();
        log.info("Mass mailing info: [Sent mails: " + successfullySent.size() + "]");
    }

    @Override
    public void sendMassMail(String message, File photo) {
        List<TelegramUser> users = telegramUserService.findAll();
        List<TelegramUser> successfullySent = users.stream()
                .filter(telegramUser -> sendPhotoToUser(message, photo, telegramUser))
                .toList();
        log.info("Mass mailing info: [Sent mails: " + successfullySent.size() + "]");
    }

    public boolean sendMessageToUser(String message, TelegramUser telegramUser) {
        try {
            String chatId = telegramUser.getChatId().toString();
            if (StringUtils.hasText(chatId)) {
                responseHelper.sendMessageAsync(chatId, message);
                return true;
            } else {
                log.warn("chat id not found for user " + telegramUser);
                return false;
            }
        } catch (TelegramApiException ignored) {
        }
        return false;
    }

    public boolean sendPhotoToUser(String message, File photo, TelegramUser telegramUser) {
        try {
            String chatId = telegramUser.getChatId().toString();
            if (StringUtils.hasText(chatId)) {
                responseHelper.sendPhotoAsync(chatId, message, photo);
                return true;
            } else {
                log.warn("chat id not found for user " + telegramUser);
                return false;
            }
        } catch (TelegramApiException ignored) {
        }
        return false;
    }
}
