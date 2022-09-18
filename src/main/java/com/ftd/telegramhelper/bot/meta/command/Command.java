package com.ftd.telegramhelper.bot.meta.command;

import com.ftd.telegramhelper.bot.meta.value.command.ICommandValue;

import javax.validation.constraints.NotNull;

public class Command implements ICommand {

    public static Command create(ICommandValue commandValue) {
        return new Command(commandValue);
    }

    @NotNull
    private ICommandValue commandValue;

    private Command(@NotNull ICommandValue commandValue) {
        if (commandValue == null) {
            throw new RuntimeException("Command value cannot be null");
        }
        this.commandValue = commandValue;
    }

    private Command() {
    }

    @Override
    public ICommandValue getCommandValue() {
        return commandValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return getCommandValue().equals(command.getCommandValue());
    }

    @Override
    public int hashCode() {
        return getCommandValue().hashCode();
    }
}
