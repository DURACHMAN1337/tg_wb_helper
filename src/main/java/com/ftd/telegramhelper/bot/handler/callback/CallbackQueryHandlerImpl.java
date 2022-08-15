package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallbackQueryHandlerImpl implements CallbackQueryHandler {


    private final WebApplicationContext webApplicationContext;

    @Autowired
    public CallbackQueryHandlerImpl(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser) {
        //chat_id Главного канала(Получилось отправлять туда сообщения)
        String chatId = "-1001720487296";
        String chatId1 = "@test1337123";
        SendMessage sendMessage = null;
        if (Callback.FIRST.equals(callbackQuery.getData())) {
            sendMessage = InlineKeyboardMarkupBuilder.create(chatId1)
                    .buildAsSendMessage();

            sendMessage.setText("test text");
            sendMessage.setChatId(chatId1);

        }
        try {
            webApplicationContext.getBean(LongPollingBot.class).execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return sendMessage;
    }
}
