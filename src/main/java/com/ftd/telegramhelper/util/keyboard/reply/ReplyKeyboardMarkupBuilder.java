package com.ftd.telegramhelper.util.keyboard.reply;

import com.ftd.telegramhelper.util.keyboard.KeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ReplyKeyboardMarkupBuilder implements KeyboardMarkupBuilder {

    private String chatId;
    private String text;

    private final List<KeyboardRow> keyboard = new ArrayList<>();
    private KeyboardRow row;

    /**
     * Use this to create new instace
     * @param chatId chat id
     * @param text text
     * @return {@link ReplyKeyboardMarkupBuilder}
     */
    public static ReplyKeyboardMarkupBuilder create(String chatId, String text) {
        return new ReplyKeyboardMarkupBuilder(chatId, text);
    }

    // private constructors
    private ReplyKeyboardMarkupBuilder(){}

    private ReplyKeyboardMarkupBuilder(String chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    @Override
    public ReplyKeyboardMarkupBuilder setChatId(String chatId) {
        this.chatId = chatId;
        return this;
    }

    @Override
    public ReplyKeyboardMarkupBuilder setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public ReplyKeyboardMarkupBuilder row() {
        this.row = new KeyboardRow();
        return this;
    }

    @Override
    public ReplyKeyboardMarkupBuilder endRow() {
        this.keyboard.add(this.row);
        this.row = null;
        return this;
    }

    @Override
    public SendMessage buildAsSendMessage() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    public ReplyKeyboardMarkupBuilder button(String text) {
        row.add(text);
        return this;
    }

    public ReplyKeyboardMarkupBuilder buttonWithContactRequest(String text) {
        KeyboardButton button = new KeyboardButton();
        button.setRequestContact(true);
        button.setText(text);
        row.add(button);
        return this;
    }
}
