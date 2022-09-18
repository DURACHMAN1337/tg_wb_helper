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
import com.ftd.telegramhelper.util.faq.FaqInfos.VaacumatorFaqInfos;
import com.ftd.telegramhelper.util.faq.KnownFaq;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.money"))
                .endRow()

                .row()
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.help"))
                .endRow()

                .row()
                .button(messageBundle.loadMessage("ftd.telegram_helper.command.faq.vaacumator"))
                .endRow()

                .buildAsSendMessage();
    }

    public PartialBotApiMethod<?> updateReplyKeyboardMarkup(TelegramUser telegramUser, String chatId, Command command) throws TelegramApiException {
        if (commands.getKnownCommands().contains(command)) {
            // start
            if (command.equals(commands.getStartCommand())) {
                setSuitableState(UserStates.NEW, telegramUser);
                return createMainMenu(chatId, true);
            }

            // vaacumator faq
            if (commands.getKnownFaqCommands().contains(command)) {
                setSuitableState(UserStates.READ_FAQ, telegramUser);
                return processFaqCommand(chatId, command);
            }

            // money
            if (command.equals(commands.getMoneyCommand())) {
                try {
                    createFeedbackIfNeeded(telegramUser);
                    setSuitableState(UserStates.IN_MENU, telegramUser);
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
                setSuitableState(UserStates.IN_MENU, telegramUser);
                return createMainMenu(chatId, false);
            }

            // stop chatting
            if (command.equals(commands.getStopChattingCommand())) {
                setSuitableState(UserStates.IN_MENU, telegramUser);
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

    private PartialBotApiMethod<?> processFaqCommand(String chatId, Command command) throws TelegramApiException {
        if (command.equals(commands.getVaacumatorFaqCommand())) {
            return createVaacumatorFaqReply(chatId);
        } else if (command.equals(commands.getVaacumatorFirstFaqCommand())) {
            return createVaacumatorFaqStepMessage(chatId, KnownFaq.VAACUMATOR_1);
        } else if (command.equals(commands.getVaacumatorSecondFaqCommand())) {
            return createVaacumatorFaqStepMessage(chatId, KnownFaq.VAACUMATOR_2);
        } else {
            handleError(Long.valueOf(chatId));
            return null;
        }
    }

    private SendMessage createVaacumatorFaqReply(String chatId) {
        return ReplyKeyboardMarkupBuilder
                .create(chatId, "Для того, чтобы разобраться, как пользоваться упаковщиком, нужно изучить всего 2️⃣ момента")
                .row()
                .button(commands.getVaacumatorFirstFaqCommand().getMessage())
                .endRow()
                .row()
                .button(commands.getVaacumatorSecondFaqCommand().getMessage())
                .endRow()
                .row()
                .button(commands.getMainMenuCommand().getMessage())
                .endRow()
                .buildAsSendMessage();
    }

    private SendPhoto createVaacumatorFaqStepMessage(String chatId, KnownFaq vaacumatorFaq) {
        String text = "";
        String nextStepCallback = "";

        switch (vaacumatorFaq) {
            case VAACUMATOR_1 -> {
                text = messageBundle.loadMessage(VaacumatorFaqInfos.FirstFaqInfo.StepMessages.FIRST_STEP_MESSAGE_KEY);
                nextStepCallback = VaacumatorFaqInfos.FirstFaqInfo.StepCallbacks.SECOND_STEP_CALLBACK;
            }
            case VAACUMATOR_2 -> {
                text = messageBundle.loadMessage(VaacumatorFaqInfos.SecondFaqInfo.StepMessages.FIRST_STEP_MESSAGE_KEY);
                nextStepCallback = VaacumatorFaqInfos.SecondFaqInfo.StepCallbacks.SECOND_STEP_CALLBACK;
            }
        }

        return InlineKeyboardMarkupBuilder
                .create(chatId)
                .setText(text)
                .row()
                .button("Следующий шаг", nextStepCallback)
                .endRow()
                .buildAsSendPhoto(
                        new InputFile(
                                KnownFaq.VAACUMATOR_1.equals(vaacumatorFaq)
                                        ? FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepImages.loadStepImage(1)
                                        : KnownFaq.VAACUMATOR_2.equals(vaacumatorFaq)
                                        ? FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepImages.loadStepImage(1)
                                        : null,
                                "vaacumator-faq-image-1"
                        )
                );
    }

    public PartialBotApiMethod<?> processFaqCallback(CallbackQuery callback) throws TelegramApiException {
        String callbackData = callback.getData();
        KnownFaq knownFaq;

        if (Callback.Faq.Vaacumator.First.ALL_STEP_CALLBACKS.contains(callbackData)) {
            knownFaq = KnownFaq.VAACUMATOR_1;
        } else if (Callback.Faq.Vaacumator.Second.ALL_STEP_CALLBACKS.contains(callbackData)) {
            knownFaq = KnownFaq.VAACUMATOR_2;
        } else {
            handleError(callback.getMessage().getChatId());
            return null;
        }

        switch (knownFaq) {
            case VAACUMATOR_1 -> {
                return processVaacumatorFaq(callback, KnownFaq.VAACUMATOR_1);
            }
            case VAACUMATOR_2 -> {
                return processVaacumatorFaq(callback, KnownFaq.VAACUMATOR_2);
            }
            default -> {
                handleError(callback.getMessage().getChatId());
                return null;
            }
        }
    }

    private SendPhoto processVaacumatorFaq(CallbackQuery callback, KnownFaq vaacumatorFaq) throws TelegramApiException {
        Message message = callback.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder = InlineKeyboardMarkupBuilder.create(String.valueOf(chatId)).row();

        switch (vaacumatorFaq) {

            case VAACUMATOR_1 -> {
                int stepNumber = getVaacumatorFaqStepNumber(callback, KnownFaq.VAACUMATOR_1);
                execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
                if (stepNumber != 0) {
                    InlineKeyboardButton previousPage = new InlineKeyboardButton("Предыдущий шаг");
                    previousPage.setCallbackData(Callback.Faq.Vaacumator.First.ALL_STEP_CALLBACKS.get(stepNumber - 1));
                    inlineKeyboardMarkupBuilder.button(previousPage);
                }
                if (stepNumber < VaacumatorFaqInfos.FirstFaqInfo.STEPS_COUNT - 1) {
                    InlineKeyboardButton nextPage = new InlineKeyboardButton("Следующий шаг");
                    nextPage.setCallbackData(Callback.Faq.Vaacumator.First.ALL_STEP_CALLBACKS.get(stepNumber + 1));
                    inlineKeyboardMarkupBuilder.button(nextPage);
                }
                return inlineKeyboardMarkupBuilder
                        .setText(messageBundle.loadMessage(VaacumatorFaqInfos.FirstFaqInfo.StepMessages.KNOWN_FAQ_MESSAGES_KEYS.get(stepNumber)))
                        .endRow()
                        .buildAsSendPhoto(
                                new InputFile(
                                        FaqInfos.VaacumatorFaqInfos.FirstFaqInfo.StepImages.loadStepImage(stepNumber + 1),
                                        "vaacumator-first-faq-image-" + stepNumber
                                )
                        );
            }

            case VAACUMATOR_2 -> {
                int stepNumber = getVaacumatorFaqStepNumber(callback, KnownFaq.VAACUMATOR_2);
                execute(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
                if (stepNumber != 0) {
                    InlineKeyboardButton previousPage = new InlineKeyboardButton("Предыдущий шаг");
                    previousPage.setCallbackData(Callback.Faq.Vaacumator.Second.ALL_STEP_CALLBACKS.get(stepNumber - 1));
                    inlineKeyboardMarkupBuilder.button(previousPage);
                }
                if (stepNumber < VaacumatorFaqInfos.SecondFaqInfo.STEPS_COUNT - 1) {
                    InlineKeyboardButton nextPage = new InlineKeyboardButton("Следующий шаг");
                    nextPage.setCallbackData(Callback.Faq.Vaacumator.Second.ALL_STEP_CALLBACKS.get(stepNumber + 1));
                    inlineKeyboardMarkupBuilder.button(nextPage);
                }
                return inlineKeyboardMarkupBuilder
                        .setText(messageBundle.loadMessage(VaacumatorFaqInfos.SecondFaqInfo.StepMessages.KNOWN_FAQ_MESSAGES_KEYS.get(stepNumber)))
                        .endRow()
                        .buildAsSendPhoto(
                                new InputFile(
                                        FaqInfos.VaacumatorFaqInfos.SecondFaqInfo.StepImages.loadStepImage(stepNumber + 1),
                                        "vaacumator-first-faq-image-" + stepNumber
                                )
                        );
            }

            default -> {
                handleError(message.getChatId());
                return null;
            }
        }
    }

    private int getVaacumatorFaqStepNumber(CallbackQuery callback, KnownFaq vaacumatroFaq) {
        String callbackData = callback.getData();
        List<String> allStepCallbacks = KnownFaq.VAACUMATOR_1.equals(vaacumatroFaq)
                ? Callback.Faq.Vaacumator.First.ALL_STEP_CALLBACKS
                : KnownFaq.VAACUMATOR_2.equals(vaacumatroFaq)
                ? Callback.Faq.Vaacumator.Second.ALL_STEP_CALLBACKS
                : new ArrayList<>();

        for (int i = 0; i < allStepCallbacks.size(); i++) {
            if (allStepCallbacks.get(i).equals(callbackData)) {
                return i;
            }
        }

        return -1;
    }

    public SendMessage processHelpCommand(String chatId) throws TelegramApiException {
        sendMessage(chatId, getHelpMessage());
        return createChatWithManager(chatId);
    }

    public SendMessage processMoneyCommand(String chatId) {
        return InlineKeyboardMarkupBuilder
                .create(chatId)
                .setText(getInfoMessage())
                .row()
                .button("Ознакомился, начать чат", Callback.START_CHATTING)
                .endRow()
                .buildAsSendMessage();
    }

    public SendMessage createChatWithManager(String chatId) {
        return ReplyKeyboardMarkupBuilder
                .create(chatId, "Чтобы выйти из режима чата, нажмите " + commands.getStopChattingCommand().getMessage())
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
