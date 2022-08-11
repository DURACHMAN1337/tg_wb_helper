package com.ftd.tg_wb_helper.bot.facade;

import com.ftd.tg_wb_helper.bot.handler.impl.CallbackQueryHandlerImpl;
import com.ftd.tg_wb_helper.bot.handler.impl.MessageHandlerImpl;
import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import com.ftd.tg_wb_helper.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class TelegramFacadeImpl implements TelegramFacade {


    @Autowired
    private CallbackQueryHandlerImpl callbackQueryHandler;
    @Autowired
    private MessageHandlerImpl messageHandler;
    @Autowired
    private TelegramUserService telegramUserService;


    @Override
    public PartialBotApiMethod<?> processUpdate(Update update) {
        if (update == null) {
            return null;
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            TelegramUser telegramUser = telegramUserService.findBy(callbackQuery.getFrom().getId());
            if (telegramUser != null)
            return callbackQueryHandler.processCallbackQuery(callbackQuery,telegramUser);
        }
        if (update.hasMessage()) {
            return messageHandler.processMessage(update.getMessage());
        }
        return null;
    }
}
