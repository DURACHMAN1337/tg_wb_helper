package com.ftd.telegramhelper.bot.meta.callback;

import com.ftd.telegramhelper.bot.meta.value.callback.ICallbackValue;

import javax.validation.constraints.NotNull;

public interface ICallback {
    @NotNull ICallbackValue getCallbackValue();
}
