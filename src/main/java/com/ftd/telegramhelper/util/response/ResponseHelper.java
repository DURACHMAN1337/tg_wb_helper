package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.bot.meta.callback.ICallback;
import com.ftd.telegramhelper.bot.meta.callback.KnownCallbacks;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.message.MessageKeys;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.keyboard.reply.ReplyKeyboardMarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseHelper {

    private final ApplicationContext applicationContext;
    private final MessageBundle messageBundle;

    @Autowired
    public ResponseHelper(ApplicationContext applicationContext, MessageBundle messageBundle) {
        this.applicationContext = applicationContext;
        this.messageBundle = messageBundle;
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> void execute(Method method)
            throws TelegramApiException {
        getBot().execute(method);
    }

    public void execute(SendPhoto sendPhoto) throws TelegramApiException {
        getBot().execute(sendPhoto);
    }

    public void execute(SendDocument sendDocument) throws TelegramApiException {
        getBot().execute(sendDocument);
    }

    public SendMessage createMainMenu(String chatId, boolean replyMode) {
        return replyMode
                ? createReplyMainMenu(chatId)
                : createInlineMainMenu(chatId);
    }

    public EditMessageText recreateMainMenu(String chatId, int messageId, String text) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), text)
                .row()
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public void updateReplyMarkup(String chatId, String text) throws TelegramApiException {
        execute(
                ReplyKeyboardMarkupBuilder
                        .create(chatId, text)
                        .row()
                        .endRow()
                        .buildAsSendMessage()
        );
    }

    public InlineKeyboardButton createSuccessButton() {
        return createButton(getSuccessMessage(), KnownCallbacks.SUCCESS_CALLBACK);
    }

    public InlineKeyboardButton createFailureButton() {
        return createButton(getFailureMessage(), KnownCallbacks.FAILURE_CALLBACK);
    }

    public Message sendMessage(String chatId, String message) throws TelegramApiException {
        return applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .build()
        );
    }

    public Message replyToMessage(String chatId, int replyToMessageId, String message) throws TelegramApiException {
        return applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .replyToMessageId(replyToMessageId)
                        .text(message)
                        .build()
        );
    }

    public void replyToMessage(
            String chatId,
            int replyToMessageId,
            File photoOrDocument,
            boolean isDocument,
            InlineKeyboardButton... buttons
    ) throws TelegramApiException {
        final PartialBotApiMethod<Message> method = isDocument
                // build send document
                ? SendDocument
                .builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .document(new InputFile().setMedia(photoOrDocument))
                .replyMarkup(createMarkupKeyboard(buttons))
                .build()
                // build send photo
                : SendPhoto
                .builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .photo(new InputFile().setMedia(photoOrDocument))
                .replyMarkup(createMarkupKeyboard(buttons))
                .build();

        if (method instanceof SendDocument) {
            execute(((SendDocument) method));
        } else {
            execute((SendPhoto) method);
        }
    }

    public InlineKeyboardButton createButton(String text, ICallback callback) {
        InlineKeyboardButton acceptButton = new InlineKeyboardButton(text);
        acceptButton.setCallbackData(callback.getCallbackValue().getValue());
        return acceptButton;
    }

    public void handleError(String chatId) throws TelegramApiException {
        applicationContext.getBean(LongPollingBot.class).execute(
                createErrorResponse(
                        createFakeCallbackQuery(chatId)
                )
        );
    }

    public SendMessage createErrorResponse(CallbackQuery callbackQuery) {
        return SendMessage
                .builder()
                .chatId(getChatId(callbackQuery))
                .text(getErrorMessage())
                .build();
    }

    private SendMessage createReplyMainMenu(String chatId) {
        return ReplyKeyboardMarkupBuilder
                .create(String.valueOf(chatId), getWelcomeMessage())
                .row()
                .endRow()
                .buildAsSendMessage();
    }

    private SendMessage createInlineMainMenu(String chatId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), getWelcomeMessage())
                .row()
                .endRow()
                .buildAsSendMessage();
    }

    private String getSuccessMessage() {
        return getMessageFromBundle(MessageKeys.SUCCESS_MESSAGE_KEY);
    }

    private String getFailureMessage() {
        return getMessageFromBundle(MessageKeys.FAILURE_MESSAGE_KEY);
    }

    private String getErrorMessage() {
        return getMessageFromBundle(MessageKeys.ERROR_MESSAGE_KEY);
    }

    private String getWelcomeMessage() {
        return getMessageFromBundle(MessageKeys.WELCOME_MESSAGE_KEY);
    }

    private String getMessageFromBundle(String messageKey) {
        return messageBundle.loadMessage(messageKey);
    }

    private LongPollingBot getBot() {
        return applicationContext.getBean(LongPollingBot.class);
    }

    @Nullable
    private InlineKeyboardMarkup createMarkupKeyboard(InlineKeyboardButton... buttons) {
        if (buttons.length == 0) {
            return null;
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>(List.of(buttons));
        keyboardMarkup.setKeyboard(new ArrayList<>());
        keyboardMarkup.getKeyboard().add(row);

        return keyboardMarkup;
    }

    private CallbackQuery createFakeCallbackQuery(String chatId) {
        CallbackQuery fakeCallback = new CallbackQuery();
        Message fakeCallbackMessage = new Message();
        fakeCallbackMessage.setChat(new Chat(Long.valueOf(chatId), "private"));
        fakeCallback.setMessage(fakeCallbackMessage);
        return fakeCallback;
    }

    private String getChatId(CallbackQuery callbackQuery) {
        return String.valueOf(callbackQuery.getMessage().getChatId());
    }
}
