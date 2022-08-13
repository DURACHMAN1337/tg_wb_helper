package com.ftd.telegramhelper.util.keyboard;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface KeyboardMarkupBuilder {

    KeyboardMarkupBuilder setChatId(String chatId);

    KeyboardMarkupBuilder setText(String text);

    KeyboardMarkupBuilder row();

    KeyboardMarkupBuilder endRow();

    SendMessage buildAsSendMessage();
}
