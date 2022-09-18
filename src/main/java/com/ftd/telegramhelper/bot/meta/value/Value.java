package com.ftd.telegramhelper.bot.meta.value;

import javax.validation.constraints.NotNull;

public interface Value<T> {
    @NotNull T getValue();
}
