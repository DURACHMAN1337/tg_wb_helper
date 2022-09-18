package com.ftd.telegramhelper.bot.meta.value.command;

import com.ftd.telegramhelper.bot.meta.value.callback.CallbackValue;

import javax.validation.constraints.NotNull;

public class CommandValue implements ICommandValue {

    public static CommandValue create(@NotNull String value) {
        return new CommandValue(value);
    }

    private String value;

    private CommandValue(String value) {
        if (value == null) {
            throw new RuntimeException("Value cannot be null");
        }
        this.value = value;
    }

    private CommandValue() {
    }

    @Override
    public String getValue() {
        return value;
    }
}
