package com.ftd.telegramhelper.bot.meta.command;

import com.ftd.telegramhelper.bot.meta.value.command.ICommandValue;

import javax.validation.constraints.NotNull;

public interface ICommand {
    @NotNull ICommandValue getCommandValue();
}
