package com.ftd.telegramhelper.bot.service.callback;

import com.ftd.telegramhelper.bot.meta.callback.ICallback;
import com.ftd.telegramhelper.bot.meta.callback.KnownCallbacks;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallbackService implements ICallbackService {

    @Override
    public List<ICallback> getKnownCallbacks() {
        return KnownCallbacks.KNOWN_CALLBACKS;
    }

    @Override
    public boolean isKnownCallback(ICallback callback) {
        return getKnownCallbacks().contains(callback);
    }
}
