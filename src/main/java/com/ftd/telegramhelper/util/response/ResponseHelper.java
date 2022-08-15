package com.ftd.telegramhelper.util.response;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ResponseHelper {

    private final ApplicationContext applicationContext;
    private final MessageBundle messageBundle;

    @Autowired
    public ResponseHelper(ApplicationContext applicationContext, MessageBundle messageBundle) {
        this.applicationContext = applicationContext;
        this.messageBundle = messageBundle;
    }

    public SendMessage createMainMenu(String chatId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), messageBundle.loadMessage("ftd.telegram_helper.message.welcome"))
                .row()
                .button("1️⃣", "1")
                .button("2️⃣", "2")
                .button("3️⃣", "3")
                .endRow()
                .buildAsSendMessage();
    }

    public EditMessageText recreateMainMenu(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId), messageBundle.loadMessage("ftd.telegram_helper.message.welcome"))
                .row()
                .button("1️⃣", "1")
                .button("2️⃣", "2")
                .button("3️⃣", "3")
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public EditMessageText createInfoPage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder.create(
                        chatId,
                        messageBundle.loadMessage("ftd.telegram_helper.message.info")
                )
                .row()
                .button("Назад", "back")
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public EditMessageText createHelpPage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder.create(
                        chatId,
                        messageBundle.loadMessage("ftd.telegram_helper.message.help")
                )
                .row()
                .button("Назад", Callback.BACK)
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

    public void replyToMessage(String chatId, String replyToMessageId, String message) throws TelegramApiException {
        applicationContext.getBean(LongPollingBot.class).execute(
                SendMessage.builder()
                        .chatId(chatId)
                        .replyToMessageId(Integer.parseInt(replyToMessageId))
                        .text(message)
                        .build()
        );
    }

    public void replyToUserMessage(String chatId,
                                          String message,
                                          TelegramUser telegramUser) throws TelegramApiException {
        SendMessage sendMessage = InlineKeyboardMarkupBuilder.create(chatId, message)
                .row()
                .button("OK", Callback.SUCCESS)
                .button("Cancel", Callback.DENIED)
                .endRow()
                .buildAsSendMessage();
        sendMessage.setReplyToMessageId(Integer.valueOf(telegramUser.getFeedbackMessageId()));
        applicationContext.getBean(LongPollingBot.class).execute(sendMessage);
    }

    public SendMessage createErrorResponse(CallbackQuery callbackQuery) {
        return SendMessage.builder()
                .chatId(getChatId(callbackQuery))
                .text("Something goes wrong, sorry...")
                .build();
    }

    public SendMessage createSuccessMessage(String chatId){
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageBundle.loadMessage("ftd.telegram_helper.message.success"))
                .build();
    }

    public SendMessage createDeniedMessage(String chatId){
        return SendMessage.builder()
                .chatId(chatId)
                .text(messageBundle.loadMessage("ftd.telegram_helper.message.failure"))
                .build();
    }

    public EditMessageText createInstructionMessage(String chatId, int messageId) {
        return InlineKeyboardMarkupBuilder.create(chatId, messageBundle.loadMessage("ftd.telegram_helper.message.details"))
                .row()
                .button("Назад", Callback.BACK)
                .endRow()
                .rebuildAsEditMessageText(messageId);
    }

    public SendMessage createUnknownCallbackResponse(CallbackQuery callbackQuery) {
        return SendMessage.builder()
                .chatId(getChatId(callbackQuery))
                .text("Unknown callback has been detected [" + callbackQuery.getData() + "]")
                .build();
    }

    private String getChatId(CallbackQuery callbackQuery) {
        return String.valueOf(callbackQuery.getMessage().getChatId());
    }

}
