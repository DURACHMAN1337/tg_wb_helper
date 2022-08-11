package com.ftd.tg_wb_helper.bot.handler.impl;

import com.ftd.tg_wb_helper.bot.handler.CallbackQueryHandler;
import com.ftd.tg_wb_helper.bot.keyboard.impl.InlineKeyboardMarkupBuilder;
import com.ftd.tg_wb_helper.model.entity.TelegramUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser) {
        //chat_id Главного канала(Получилось отправлять туда сообщения)
        String chatId = "-1001720487296";
        String chatId1 = "@test1337123";
        if (callbackQuery.getData().equals("1")) {
            SendMessage sendMessage = InlineKeyboardMarkupBuilder.create(chatId1)
                    .row()
                    .button("Da","da")
                    .button("Manda","net")
                    .endRow()
                    .buildAsSendMessage();
            sendMessage.setText("test text");
            sendMessage.setChatId("@test1337123");

            return sendMessage;
        }
        return null;
    }
}
