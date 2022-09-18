package com.ftd.telegramhelper.bot.meta.value.callback;

import javax.validation.constraints.NotNull;

public class CallbackValue implements ICallbackValue {

    public static CallbackValue create(@NotNull String value) {
        return new CallbackValue(value);
    }

    private String value;

    private CallbackValue(String value) {
        if (value == null) {
            throw new RuntimeException("Value cannot be null");
        }
        this.value = value;
    }

    private CallbackValue() {
    }

    @Override
    public String getValue() {
        return value;
    }
}
