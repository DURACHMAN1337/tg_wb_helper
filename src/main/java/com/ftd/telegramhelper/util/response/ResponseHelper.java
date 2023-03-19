package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.keyboard.reply.ReplyKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.message.Smiles;
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
import java.util.concurrent.CompletableFuture;

@Component
public class ResponseHelper {

    private final ApplicationContext applicationContext;
    private final MessageBundle messageBundle;

    @Autowired
    public ResponseHelper(ApplicationContext applicationContext, MessageBundle messageBundle) {
        this.applicationContext = applicationContext;
        this.messageBundle = messageBundle;
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> void execute(
            Method method
    ) throws TelegramApiException {
        getBot().execute(method);
    }

    public void execute(SendPhoto sendPhoto) throws TelegramApiException {
        getBot().execute(sendPhoto);
    }

    public void execute(SendDocument sendDocument) throws TelegramApiException {
        getBot().execute(sendDocument);
    }

    public SendMessage createMainAdminMenu(String chatId, String caption) {
        return InlineKeyboardMarkupBuilder
                .create(chatId, caption)
                .row()
                .button("Отправить сообщение", Callback.SEND_MASS_MAIL)
                .endRow()
                .row()
                .button("Сменить пароль", Callback.CHANGE_ADMIN_PANEL_PASSWORD)
                .button("Текущий пароль", Callback.CURRENT_ADMIN_PANEL_PASSWORD)
                .endRow()
                .row()
                .button("Выход", Callback.EXIT_ADMIN_PANEL)
                .endRow()
                .buildAsSendMessage();
    }

    public SendMessage createAdminMenu(String chatId, String caption) {
        return InlineKeyboardMarkupBuilder
                .create(chatId, caption)
                .row()
                .button("Отправить сообщение", Callback.SEND_MASS_MAIL)
                .endRow()
                .row()
                .button("Выход", Callback.EXIT_ADMIN_PANEL)
                .endRow()
                .buildAsSendMessage();
    }

    public SendMessage incorrectAdminPanelPassword(String chatId) {
        return new SendMessage("[ADMIN_PANEL]: Неверный пароль. Отказано в доступен", chatId);
    }

    public SendMessage createMainMenu(String chatId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), getWelcomeMessage())
                .row()
                .button(Smiles.DIGIT_ONE.getUnicode(), Callback.FIRST)
                .button(Smiles.DIGIT_TWO.getUnicode(), Callback.SECOND)
                .button(Smiles.DIGIT_THREE.getUnicode(), Callback.THIRD)
                .endRow()
                .buildAsSendMessage();
    }

    public void updateReplyMarkup(String chatId) throws TelegramApiException {
        execute(
                ReplyKeyboardMarkupBuilder
                        .create(chatId, Smiles.FIRE.getUnicode() + Smiles.FIRE.getUnicode() + Smiles.FIRE.getUnicode())
                        .row()
                        .button(Command.INSTRUCTION.getValue())
                        .endRow()
                        .buildAsSendMessage()
        );
    }

    public EditMessageText recreateMainMenu(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), getWelcomeMessage())
                .row()
                .button(Smiles.DIGIT_ONE.getUnicode(), Callback.FIRST)
                .button(Smiles.DIGIT_TWO.getUnicode(), Callback.SECOND)
                .button(Smiles.DIGIT_THREE.getUnicode(), Callback.THIRD)
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public EditMessageText createInfoPage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder
                .create(chatId, getInfoMessage())
                .row()
                .button(createBackButton())
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public EditMessageText createHelpPage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder
                .create(chatId, getHelpMessage())
                .row()
                .button(createBackButton())
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public Message sendMessage(String chatId, String message) throws TelegramApiException {
        return applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .build()
        );
    }

    public CompletableFuture<Message> sendMessageAsync(String chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build();
        return applicationContext.getBean(LongPollingBot.class).executeAsync(sendMessage);
    }

    public void sendPhotoAsync(String chatId, String message, File photo) throws TelegramApiException {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .caption(message)
                .photo(new InputFile().setMedia(photo))
                .build();
        applicationContext.getBean(LongPollingBot.class).executeAsync(sendPhoto);
    }

    public void replyToMessage(String chatId, String replyToMessageId, String message) throws TelegramApiException {
        execute(
                SendMessage
                        .builder()
                        .chatId(chatId)
                        .replyToMessageId(Integer.parseInt(replyToMessageId))
                        .text(message)
                        .build()
        );
    }

    public void replyToMessage(
            String chatId,
            String replyToMessageId,
            File photoOrDocument,
            boolean isDocument,
            InlineKeyboardButton... buttons
    ) throws TelegramApiException {
        final PartialBotApiMethod<Message> method = isDocument
                // build send document
                ? SendDocument
                .builder()
                .chatId(chatId)
                .replyToMessageId(Integer.parseInt(replyToMessageId))
                .document(new InputFile().setMedia(photoOrDocument))
                .replyMarkup(createMarkupKeyboard(buttons))
                .build()
                // build send photo
                : SendPhoto
                .builder()
                .chatId(chatId)
                .replyToMessageId(Integer.parseInt(replyToMessageId))
                .photo(new InputFile().setMedia(photoOrDocument))
                .replyMarkup(createMarkupKeyboard(buttons))
                .build();

        if (method instanceof SendDocument) {
            execute(((SendDocument) method));
        } else {
            execute((SendPhoto) method);
        }
    }

    public InlineKeyboardButton createAcceptButton() {
        InlineKeyboardButton acceptButton = new InlineKeyboardButton(
                Smiles.CREDIT_CARD.getUnicode() + " Принять"
        );
        acceptButton.setCallbackData(Callback.SUCCESS);
        return acceptButton;
    }

    public InlineKeyboardButton createRejectButton() {
        InlineKeyboardButton rejectButton = new InlineKeyboardButton(
                Smiles.RED_X_SIGN.getUnicode() + " Отклонить"
        );
        rejectButton.setCallbackData(Callback.DENIED);
        return rejectButton;
    }

    public void handleError(Long chatId) throws TelegramApiException {
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
                .text(messageBundle.loadMessage("ftd.telegram_helper.message.error"))
                .build();
    }

    public SendMessage createSuccessMessage(String chatId) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(messageBundle.loadMessage("ftd.telegram_helper.message.success"))
                .build();
    }

    public SendMessage createDeniedMessage(String chatId) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text(messageBundle.loadMessage("ftd.telegram_helper.message.failure"))
                .build();
    }

    public EditMessageText createInstructionMessage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder
                .create(chatId, messageBundle.loadMessage("ftd.telegram_helper.message.details"))
                .row()
                .button(createBackButton())
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    private LongPollingBot getBot() {
        return applicationContext.getBean(LongPollingBot.class);
    }

    private String getWelcomeMessage() {
        return String.format(
                messageBundle.loadMessage("ftd.telegram_helper.message.welcome"),
                Smiles.DIGIT_ONE.getUnicode(), Smiles.DIGIT_TWO.getUnicode(), Smiles.DIGIT_THREE.getUnicode()
        );
    }

    private String getInfoMessage() {
        return String.format(
                messageBundle.loadMessage("ftd.telegram_helper.message.info"),
                Smiles.DIGIT_ONE.getUnicode(), Smiles.DIGIT_TWO.getUnicode(), Smiles.DIGIT_THREE.getUnicode(),
                Smiles.DIGIT_FOUR.getUnicode(), Smiles.DIGIT_FIVE.getUnicode(), Smiles.DIGIT_SIX.getUnicode(),
                Smiles.DIGIT_SEVEN.getUnicode()
        );
    }

    private String getHelpMessage() {
        return messageBundle.loadMessage("ftd.telegram_helper.message.help");
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

    private InlineKeyboardButton createBackButton() {
        InlineKeyboardButton backButton = new InlineKeyboardButton(Smiles.BACK.getUnicode() + " Назад");
        backButton.setCallbackData(Callback.BACK);
        return backButton;
    }

    private CallbackQuery createFakeCallbackQuery(Long chatId) {
        CallbackQuery fakeCallback = new CallbackQuery();
        Message fakeCallbackMessage = new Message();
        fakeCallbackMessage.setChat(new Chat(chatId, "private"));
        fakeCallback.setMessage(fakeCallbackMessage);
        return fakeCallback;
    }

    private String getChatId(CallbackQuery callbackQuery) {
        return String.valueOf(callbackQuery.getMessage().getChatId());
    }

    public SendMessage changeAdminPasswordRequest(String chatId) {
        return new SendMessage(chatId, "Send new password");
    }

    public SendMessage currentAdminPassword(String chatId, String password) {
        return new SendMessage(chatId, "Current password is '" + password + "'");
    }

    public SendMessage massMailingRequest(String chatId) {
        return new SendMessage(chatId, messageBundle.loadMessage("ftd.telegram_helper.massMailRequest.caption"));
    }

    public SendMessage massMailingSuccessfullySent(Long chatId, boolean isMainAdmin) {
        return isMainAdmin
                ? createMainAdminMenu(chatId.toString(), "Mass mailing was successfully sent")
                : createAdminMenu(chatId.toString(), "Mass mailing was successfully sent");
    }

    public SendMessage adminPasswordSuccessfullyChanged(String chatId, String newPassword) {
        return new SendMessage(chatId, "Password has benn changed. New password is: '" + newPassword + "'");
    }
}
