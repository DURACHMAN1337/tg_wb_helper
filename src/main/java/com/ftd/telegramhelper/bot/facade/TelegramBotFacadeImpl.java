package com.ftd.telegramhelper.bot.facade;

import com.ftd.telegramhelper.bot.handler.callback.CallbackQueryHandlerImpl;
import com.ftd.telegramhelper.bot.handler.message.MessageHandlerImpl;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.telegramuser.TelegramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBotFacadeImpl implements TelegramBotFacade {

    private final CallbackQueryHandlerImpl callbackQueryHandler;
    private final MessageHandlerImpl messageHandler;
    private final TelegramUserService telegramUserService;

    @Autowired
    public TelegramBotFacadeImpl(CallbackQueryHandlerImpl callbackQueryHandler, MessageHandlerImpl messageHandler, TelegramUserService telegramUserService) {
        this.callbackQueryHandler = callbackQueryHandler;
        this.messageHandler = messageHandler;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public PartialBotApiMethod<?> processUpdate(Update update) {
        if (update == null) {
            return null;
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            TelegramUser telegramUser = telegramUserService.findBy(callbackQuery.getFrom().getId());
            if (telegramUser == null)
                telegramUser = telegramUserService.createAndSaveFrom(callbackQuery.getFrom(), callbackQuery.getMessage().getChatId());
            return callbackQueryHandler.processCallbackQuery(callbackQuery, telegramUser);
        }
        if (update.hasMessage()) {
            return messageHandler.processMessage(update.getMessage());
        }
        return null;
    }
}
