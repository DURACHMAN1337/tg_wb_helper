package com.ftd.telegramhelper.exception;

public class TelegramUserNotExistException extends Exception {
    public TelegramUserNotExistException() {
        super("Telegram user not exist");
    }
}
