package com.ftd.telegramhelper.bot.service.callback;

import com.ftd.telegramhelper.bot.meta.callback.ICallback;

import java.util.List;

public interface ICallbackService {
    List<ICallback> getKnownCallbacks();

    boolean isKnownCallback(ICallback callback);
}
