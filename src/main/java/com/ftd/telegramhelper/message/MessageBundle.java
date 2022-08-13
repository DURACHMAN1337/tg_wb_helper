package com.ftd.telegramhelper.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageBundle {

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;


    public String loadMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    public String loadMessage(String key) {
        return messageSource.getMessage(key, null, new Locale("ru"));
    }
}
