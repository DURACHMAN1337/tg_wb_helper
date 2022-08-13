package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser) {
        //chat_id Главного канала(Получилось отправлять туда сообщения)
        String chatId = "-1001720487296";
        String chatId1 = "@test1337123";

        if (Callback.FIRST.equals(callbackQuery.getData())) {
            SendMessage sendMessage = InlineKeyboardMarkupBuilder.create(chatId1)
                    .row()
                    .button("Da", "da")
                    .button("Manda", "net")
                    .endRow()
                    .buildAsSendMessage();

            sendMessage.setText("test text");
            sendMessage.setChatId("@test1337123");

            return sendMessage;
        }

        return null;
    }
}
