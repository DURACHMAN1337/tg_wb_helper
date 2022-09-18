package com.ftd.telegramhelper.util.command;


import com.ftd.telegramhelper.message.MessageBundle;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public final class Commands {

    private static final String START_COMMAND_KEY = "ftd.telegram_helper.command.start";
    private static final String MAIN_MENU_COMMAND_KEY = "ftd.telegram_helper.command.mainMenu";
    private static final String STOP_CHATTING_COMMAND_KEY = "ftd.telegram_helper.command.stopChatting";
    private static final String VAACUMATOR_FAQ_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator";
    private static final String VAACUMATOR_FIRST_FAQ_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.first";
    private static final String VAACUMATOR_SECOND_FAQ_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.second";
    private static final String MONEY_COMMAND_KEY = "ftd.telegram_helper.command.money";
    private static final String HELP_COMMAND_KEY = "ftd.telegram_helper.command.help";

    private static final List<String> KNOWN_COMMANDS_KEYS = Arrays.asList(
            START_COMMAND_KEY, MAIN_MENU_COMMAND_KEY, STOP_CHATTING_COMMAND_KEY, MONEY_COMMAND_KEY, HELP_COMMAND_KEY,
            VAACUMATOR_FAQ_COMMAND_KEY, VAACUMATOR_FIRST_FAQ_COMMAND_KEY, VAACUMATOR_SECOND_FAQ_COMMAND_KEY
    );

    private final MessageBundle messageBundle;

    public Commands(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
    }

    public Command getStartCommand() {
        return Command.create(messageBundle.loadMessage(START_COMMAND_KEY));
    }

    public Command getMainMenuCommand() {
        return Command.create(messageBundle.loadMessage(MAIN_MENU_COMMAND_KEY));
    }

    public Command getStopChattingCommand() {
        return Command.create(messageBundle.loadMessage(STOP_CHATTING_COMMAND_KEY));
    }

    public List<Command> getKnownFaqCommands() {
        return Arrays.asList(
                getVaacumatorFaqCommand(),
                getVaacumatorFirstFaqCommand(),
                getVaacumatorSecondFaqCommand()
        );
    }

    public Command getVaacumatorFaqCommand() {
        return Command.create(messageBundle.loadMessage(VAACUMATOR_FAQ_COMMAND_KEY));
    }

    public Command getVaacumatorFirstFaqCommand() {
        return Command.create(messageBundle.loadMessage(VAACUMATOR_FIRST_FAQ_COMMAND_KEY));
    }

    public Command getVaacumatorSecondFaqCommand() {
        return Command.create(messageBundle.loadMessage(VAACUMATOR_SECOND_FAQ_COMMAND_KEY));
    }

    public Command getHelpCommand() {
        return Command.create(messageBundle.loadMessage(HELP_COMMAND_KEY));
    }

    public Command getMoneyCommand() {
        return Command.create(messageBundle.loadMessage(MONEY_COMMAND_KEY));
    }

    public List<Command> getKnownCommands() {
        return KNOWN_COMMANDS_KEYS
                .stream()
                .map(it -> Command.create(messageBundle.loadMessage(it)))
                .toList();
    }
}


