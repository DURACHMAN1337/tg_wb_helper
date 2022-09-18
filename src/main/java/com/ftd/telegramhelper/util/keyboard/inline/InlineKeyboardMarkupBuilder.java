package com.ftd.telegramhelper.util.keyboard.inline;

import com.ftd.telegramhelper.bot.meta.callback.KnownCallbacks;
import com.ftd.telegramhelper.util.keyboard.KeyboardMarkupBuilder;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardMarkupBuilder implements KeyboardMarkupBuilder {

    private String chatId;
    private String text = "text_place_holder";
    private List<InlineKeyboardButton> row = null;
    private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    // how many buttons will be in the row
    private int rowSize = 2;

    /**
     * Use this to create new instance
     * @param chatId chat id
     * @param text text
     * @return {@link InlineKeyboardMarkupBuilder}
     */
    public static InlineKeyboardMarkupBuilder create(String chatId, String text) {
        return new InlineKeyboardMarkupBuilder(chatId, text);
    }

    /**
     * Use this to create new instance
     * @param chatId chat id
     * @return {@link InlineKeyboardMarkupBuilder}
     */
    public static InlineKeyboardMarkupBuilder create(String chatId) {
        return new InlineKeyboardMarkupBuilder(chatId);
    }

    @SuppressWarnings("unused")
    private InlineKeyboardMarkupBuilder() {}

    private InlineKeyboardMarkupBuilder(String chatId) {
        this.chatId = chatId;
    }

    private InlineKeyboardMarkupBuilder(String chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    @Override
    public InlineKeyboardMarkupBuilder setChatId(String chatId) {
        this.chatId = chatId;
        return this;
    }

    @Override
    public InlineKeyboardMarkupBuilder setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public InlineKeyboardMarkupBuilder row() {
        this.row = new ArrayList<>();
        return this;
    }

    public InlineKeyboardMarkupBuilder titleRow(@NotNull String title) {
        row();
        button(title, KnownCallbacks.FAKE_CALLBACK.getCallbackValue().getValue());
        endRow();
        return this;
    }

    @Override
    public InlineKeyboardMarkupBuilder endRow() {
        this.keyboard.add(this.row);
        this.row = null;
        return this;
    }

    /**
     * This method will send *new message*.
     * It's impossible to replace (edit) message (not SendPhoto message) by message with photo
     * @param photo photo
     * @return instance of SendPhoto
     */
    public SendPhoto buildAsSendPhoto(InputFile photo) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(text);
        sendPhoto.setReplyMarkup(keyboardMarkup);
        sendPhoto.setParseMode(ParseMode.MARKDOWN);
        sendPhoto.setProtectContent(false);
        sendPhoto.setPhoto(photo);
        return sendPhoto;
    }

    @Override
    public SendMessage buildAsSendMessage() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    public EditMessageText rebuildAsEditMessageText(int messageId) {
        EditMessageText editedMessaged = new EditMessageText();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        editedMessaged.setText(text);
        editedMessaged.setChatId(chatId);
        editedMessaged.setMessageId(messageId);
        editedMessaged.setReplyMarkup(keyboardMarkup);
        editedMessaged.enableMarkdown(true);
        return editedMessaged;
    }

    public EditMessageReplyMarkup rebuildAsEditMessageReplyMarkup(
            int messageId,
            @Nullable InlineKeyboardMarkup markup
    ) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setReplyMarkup(markup);
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);

        return editMessageReplyMarkup;
    }

    public EditMessageCaption rebuildAsEditMessageCaption(int messageId) {
        EditMessageCaption editedMessaged = new EditMessageCaption();
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        editedMessaged.setCaption(text);
        editedMessaged.setChatId(chatId);
        editedMessaged.setMessageId(messageId);
        editedMessaged.setReplyMarkup(keyboardMarkup);
        editedMessaged.setParseMode(ParseMode.MARKDOWN);
        return editedMessaged;
    }

    public DeleteMessage rebuildAsDeleteMessage(int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        return deleteMessage;
    }

    public InlineKeyboardMarkupBuilder button(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        row.add(inlineKeyboardButton);
        return this;
    }

    public InlineKeyboardMarkupBuilder button(InlineKeyboardButton button) {
        row.add(button);
        return this;
    }

    public InlineKeyboardMarkupBuilder buttons(List<InlineKeyboardButton> buttons) {
        for (InlineKeyboardButton button : buttons) {
            if (row.size() < rowSize) {
                row.add(button);
            } else {
                endRow();
                row();
                row.add(button);
            }
        }
        return this;
    }

    @SuppressWarnings("unused")
    public InlineKeyboardMarkupBuilder buttonWithURL(String text, String URL) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setUrl(URL);
        row.add(inlineKeyboardButton);
        return this;
    }

    /**
     * Set max count of buttons in the row.
     * Use this method before start build UI.
     *
     * @param rowSize max count of buttons in the row.
     */
    public void setRowSize(int rowSize) {
        if (rowSize > 0 && rowSize < Integer.MAX_VALUE) {
            this.rowSize = rowSize;
        }
    }

    public int getRowSize() {
        return this.rowSize;
    }
}
