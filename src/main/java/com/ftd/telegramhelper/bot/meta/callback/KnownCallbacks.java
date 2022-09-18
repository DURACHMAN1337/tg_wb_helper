package com.ftd.telegramhelper.bot.meta.callback;

import com.ftd.telegramhelper.bot.meta.value.callback.CallbackValue;
import com.ftd.telegramhelper.bot.meta.value.callback.ICallbackValue;

import java.util.Arrays;
import java.util.List;

public abstract class KnownCallbacks {

    public static final ICallback SUCCESS_CALLBACK = Callback.create(KnownCallbackValues.SUCCESS_CALLBACK_VALUE);
    public static final ICallback FAILURE_CALLBACK = Callback.create(KnownCallbackValues.FAILURE_CALLBACK_VALUE);
    public static final ICallback FAKE_CALLBACK = Callback.create(KnownCallbackValues.FAKE_CALLBACK_VALUE);

    public static final List<ICallback> KNOWN_CALLBACKS = Arrays.asList(
            SUCCESS_CALLBACK, FAKE_CALLBACK, FAKE_CALLBACK
    );

    private abstract static class KnownCallbackValues {

        private static final ICallbackValue SUCCESS_CALLBACK_VALUE = CallbackValue.create(KnownCallbackData.SUCCESS_CALLBACK_DATA);
        private static final ICallbackValue FAILURE_CALLBACK_VALUE = CallbackValue.create(KnownCallbackData.FAILURE_CALLBACK_DATA);
        private static final ICallbackValue FAKE_CALLBACK_VALUE = CallbackValue.create(KnownCallbackData.FAKE_CALLBACK_DATA);

    }

    private abstract static class KnownCallbackData {

        private static final String SUCCESS_CALLBACK_DATA = "successCallbackData";
        private static final String FAILURE_CALLBACK_DATA = "failureCallbackData";
        private static final String FAKE_CALLBACK_DATA = "fakeCallback_";
    }
}
