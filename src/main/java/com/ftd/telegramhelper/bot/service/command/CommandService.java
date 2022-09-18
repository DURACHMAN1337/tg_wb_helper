package com.ftd.telegramhelper.bot.service.command;

import com.ftd.telegramhelper.bot.meta.command.Command;
import com.ftd.telegramhelper.bot.meta.command.ICommand;
import com.ftd.telegramhelper.bot.meta.value.command.CommandValue;
import com.ftd.telegramhelper.bot.meta.value.command.ICommandValue;
import com.ftd.telegramhelper.message.MessageBundle;
import com.ftd.telegramhelper.message.MessageKeys;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandService implements ICommandService {

    private final MessageBundle messageBundle;

    public CommandService(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
    }

    @Override
    public List<ICommand> getKnownCommands() {
        return MessageKeys.CommandsMessageKeys.KNOWN_COMMAND_MESSAGE_KEY
                .stream()
                .map(messageKey -> (ICommand) Command.create(
                                CommandValue.create(
                                        getCommandText(messageKey)
                                )
                        )
                )
                .toList();
    }

    @Override
    public boolean isKnownCommand(ICommand command) {
        return getKnownCommands().contains(command);
    }

    @Override
    public boolean isStartCommand(ICommand command) {
        return command.equals(getStartCommand());
    }

    private ICommand getStartCommand() {
        return getCommand(getCommandValue(MessageKeys.CommandsMessageKeys.START_COMMAND));
    }

    private ICommand getCommand(ICommandValue commandValue) {
        return Command.create(commandValue);
    }

    private ICommandValue getCommandValue(String commandMessageKey) {
        return CommandValue.create(getCommandText(commandMessageKey));
    }

    private String getCommandText(String commandMessageKey) {
        return messageBundle.loadMessage(commandMessageKey);
    }
}
