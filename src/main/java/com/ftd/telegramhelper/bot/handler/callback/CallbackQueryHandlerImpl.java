package com.ftd.telegramhelper.bot.handler.callback;

import com.ftd.telegramhelper.bot.longpolling.LongPollingBot;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.telegramuser.TelegramUser;
import com.ftd.telegramhelper.util.callback.Callback;
import com.ftd.telegramhelper.util.keyboard.inline.InlineKeyboardMarkupBuilder;
import com.ftd.telegramhelper.util.response.ResponseHelper;
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
    private MessageBundle messageBundle;
    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    public CallbackQueryHandlerImpl(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public PartialBotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery, TelegramUser telegramUser) {
        String userChatId = telegramUser.getChatId().toString();
        String channelChatId = "@test1337123";
        SendMessage sendMessage = null;
        if (Callback.FIRST.equals(callbackQuery.getData())) {
            sendMessage =  responseHelper.createPostForChannel(channelChatId,telegramUser);
        }
        if (Callback.SECOND.equals(callbackQuery.getData())) {
            sendMessage = responseHelper.createInfoPage(userChatId);
        }
        if (Callback.THIRD.equals(callbackQuery.getData())){
            sendMessage = responseHelper.createHelpPage(userChatId);
        }
        if (Callback.BACK.equals(callbackQuery.getData())){
            responseHelper.createMainMenu(userChatId);
        }
        try {
            webApplicationContext.getBean(LongPollingBot.class).execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return sendMessage;
    }
}
