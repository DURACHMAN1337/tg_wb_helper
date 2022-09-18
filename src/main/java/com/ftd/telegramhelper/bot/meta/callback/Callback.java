package com.ftd.telegramhelper.bot.meta.callback;

import com.ftd.telegramhelper.bot.meta.value.callback.ICallbackValue;

import javax.validation.constraints.NotNull;

public class Callback implements ICallback {

    @NotNull
    public static Callback create(@NotNull ICallbackValue callbackValue) {
        return new Callback(callbackValue);
    }

    @NotNull
    private ICallbackValue callbackValue;

    private Callback() {
    }

    private Callback(@NotNull ICallbackValue callbackValue) {
        if (callbackValue == null) {
            throw new RuntimeException("Callback value cannot be null");
        }
        this.callbackValue = callbackValue;
    }

    @NotNull
    @Override
    public ICallbackValue getCallbackValue() {
        return callbackValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Callback callback = (Callback) o;

        return getCallbackValue().equals(callback.getCallbackValue());
    }

    @Override
    public int hashCode() {
        return getCallbackValue().hashCode();
    }
}
