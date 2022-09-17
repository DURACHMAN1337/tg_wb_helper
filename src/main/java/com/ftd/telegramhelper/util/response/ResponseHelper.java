package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.feedback.FeedbackService;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.command.Command;
import com.ftd.telegramhelper.util.command.Commands;
import com.ftd.telegramhelper.util.faq.FaqInfos;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.keyboard.reply.ReplyKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.message.Smiles;
import com.ftd.telegramhelper.util.state.UserStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
    private final Commands commands;

    @Autowired
    public ResponseHelper(
            ApplicationContext applicationContext,
            MessageBundle messageBundle,
            Commands commands
    ) {
        this.applicationContext = applicationContext;
        this.messageBundle = messageBundle;
        this.commands = commands;
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

    public SendMessage createMainMenu(String chatId, boolean sendWelcomeMessage) {
        return ReplyKeyboardMarkupBuilder
                .create(
                        String.valueOf(chatId),
                        sendWelcomeMessage ? getWelcomeMessage() : "Меню"
                )
                .row()
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.faq.vaacumator"))
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.money"))
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.help"))
                .endRow()
                .buildAsSendMessage();
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

    public SendMessage updateReplyKeyboardMarkup(TelegramUser telegramUser, String chatId, Command command) throws TelegramApiException {
        if (commands.getKnownCommands().contains(command)) {
            // start
            if (command.equals(commands.getStartCommand())) {
                setSuitableState(UserStates.NEW, telegramUser);
                return createMainMenu(chatId, true);
            }

            // vaacumator faq
            if (command.equals(commands.getVaacumatorFaqCommand())) {
                setSuitableState(UserStates.READ_FAQ_VAACUMATOR, telegramUser);
                return processVaacumatorFaqCommand(chatId);
            }

            // vaacumator faq step
            if (UserStates.READ_FAQ_VAACUMATOR.equals(telegramUser.getState())
                    && commands.getVaacumatorFaqStepCommands().contains(command)) {
                return processVaacumatorFaqStepCommand(chatId, command);
            }

            // money
            if (command.equals(commands.getMoneyCommand())) {
                try {
                    createFeedbackIfNeeded(telegramUser);
                    setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
                    return processMoneyCommand(chatId);
                } catch (Exception e) {
                    return createErrorResponse(createFakeCallbackQuery(Long.valueOf(chatId)));
                }
            }

            // help
            if (command.equals(commands.getHelpCommand())) {
                try {
                    createFeedbackIfNeeded(telegramUser);
                    setSuitableState(UserStates.CAN_SEND_MESSAGES, telegramUser);
                    return processHelpCommand(chatId);
                } catch (Exception e) {
                    return createErrorResponse(createFakeCallbackQuery(Long.valueOf(chatId)));
                }
            }

            // main_menu
            if (command.equals(commands.getMainMenuCommand())) {
                setSuitableState(UserStates.IN_PROGRESS, telegramUser);
                return createMainMenu(chatId, false);
            }

            // stop chatting
            if (command.equals(commands.getStopChattingCommand())) {
                setSuitableState(UserStates.IN_PROGRESS, telegramUser);
                sendMessage(chatId, "Вы вышли из режима общения");
                return createMainMenu(chatId, false);
            }

        }

        throw new RuntimeException("Unexpected command has been detected [" + command + "]");
    }

    private void setSuitableState(UserStates state, TelegramUser forUser) {
        forUser.setState(state);
        applicationContext.getBean(TelegramUserService.class).save(forUser);
    }

    private void createFeedbackIfNeeded(TelegramUser telegramUser) throws TelegramApiException {
        if (!StringUtils.hasText(telegramUser.getFeedbackMessageId())) {
            applicationContext.getBean(FeedbackService.class).createFeedback(telegramUser);
        }
    }

    private SendMessage processVaacumatorFaqStepCommand(String chatId, Command command) throws TelegramApiException {
        for (int stepNumber = 1; stepNumber <= commands.getVaacumatorFaqStepCommands().size(); stepNumber++) {
            if (command.equals(commands.getVaacumatorFaqStepCommand(stepNumber))) {
                execute(
                        SendPhoto
                                .builder()
                                .chatId(chatId)
                                .photo(
                                        new InputFile()
                                                .setMedia(
                                                        FaqInfos.VaacumatorFaqInfos.StepImages.loadImage(stepNumber),
                                                        "vaacumator-image-" + stepNumber)
                                )
                                .build()
                );
                return updateVaacumatorFaqStepReply(chatId, stepNumber);
            }
        }
        throw new RuntimeException("Unexpected faq step has been detected");
    }

    private SendMessage updateVaacumatorFaqStepReply(String chatId, int stepNumber) {
        return ReplyKeyboardMarkupBuilder
                .create(
                        String.valueOf(chatId),
                        messageBundle.loadMessage(
                                FaqInfos.VaacumatorFaqInfos.KNOWN_FAQ_MESSAGES_KEYS.get(stepNumber - 1)
                        )
                )
                .row()
                .button(commands.getVaacumatorFaqStepCommand(1).getMessage())
                .button(commands.getVaacumatorFaqStepCommand(2).getMessage())
                .button(commands.getVaacumatorFaqStepCommand(3).getMessage())
                .button(commands.getVaacumatorFaqStepCommand(4).getMessage())
                .endRow()
                .row()
                .button(commands.getMainMenuCommand().getMessage())
                .endRow()
                .buildAsSendMessage();
    }

    private SendMessage processVaacumatorFaqCommand(String chatId) {
        return updateVaacumatorFaqStepReply(chatId, 1);
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

    public SendMessage processHelpCommand(String chatId) {
        return ReplyKeyboardMarkupBuilder
                .create(chatId, getHelpMessage())
                .row()
                .button(commands.getStopChattingCommand().getMessage())
                .endRow()
                .buildAsSendMessage();
    }

    public SendMessage processMoneyCommand(String chatId) {
        return ReplyKeyboardMarkupBuilder
                .create(chatId, getInfoMessage())
                .row()
                .button(commands.getStopChattingCommand().getMessage())
                .endRow()
                .buildAsSendMessage();
    }

    public Message sendMessage(String chatId, String message) throws TelegramApiException {
        return applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .text(message)
                        .build()
        );
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
        return messageBundle.loadMessage("ftd.telegram_helper.message.welcome");
    }

    private String getInfoMessage() {
        return messageBundle.loadMessage("ftd.telegram_helper.message.info");
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
}
