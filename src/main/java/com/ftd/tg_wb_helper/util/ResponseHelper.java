package com.ftd.tg_wb_helper.util;

import com.ftd.tg_wb_helper.bot.keyboard.impl.InlineKeyboardMarkupBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ResponseHelper {

    public SendMessage createMainMenu(Long chatId){
       return InlineKeyboardMarkupBuilder
                .create(String.valueOf(chatId),"123")
                .row()
                .button("1","1")
                .button("2","2")
                .button("3","3")
                .endRow()
                .buildAsSendMessage();
    }
}
