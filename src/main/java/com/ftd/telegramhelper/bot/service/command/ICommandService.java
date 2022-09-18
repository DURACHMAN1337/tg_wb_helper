package com.ftd.telegramhelper.bot.service.command;

import com.ftd.telegramhelper.bot.meta.command.ICommand;

import java.util.List;

public interface ICommandService {
    List<ICommand> getKnownCommands();
    boolean isKnownCommand(ICommand command);
    boolean isStartCommand(ICommand command);
}
