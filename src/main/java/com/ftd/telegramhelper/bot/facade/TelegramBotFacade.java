package com.ftd.telegramhelper.bot.facade;

import com.ftd.telegramhelper.bot.handler.callback.CallbackQueryHandler;
import com.ftd.telegramhelper.bot.handler.message.MessageHandler;
import com.ftd.telegramhelper.exception.IncorrectFeedbackChannelPostException;
import com.ftd.telegramhelper.exception.TelegramUserNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotFacade implements ITelegramBotFacade {

    private final CallbackQueryHandler callbackQueryHandler;
    private final MessageHandler messageHandler;

    @Autowired
    public TelegramBotFacade(
            CallbackQueryHandler callbackQueryHandler,
            MessageHandler messageHandler
    ) {
        this.callbackQueryHandler = callbackQueryHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public PartialBotApiMethod<?> processUpdate(Update update)
            throws TelegramApiException, TelegramUserNotExistException, IncorrectFeedbackChannelPostException {
        if (update == null) {
            return null;
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else if (update.hasMessage()) {
            return messageHandler.processMessage(update.getMessage());
        }

        return null;
    }
}
