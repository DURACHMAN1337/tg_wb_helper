package com.ftd.telegramhelper.util.command;


import com.ftd.telegramhelper.message.MessageBundle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public final class Commands {

    private static final String START_COMMAND_KEY = "ftd.telegram_helper.command.start";
    private static final String MAIN_MENU_COMMAND_KEY = "ftd.telegram_helper.command.mainMenu";
    private static final String STOP_CHATTING_COMMAND_KEY = "ftd.telegram_helper.command.stopChatting";
    private static final String VAACUMATOR_FAQ_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator";
    private static final String MONEY_COMMAND_KEY = "ftd.telegram_helper.command.money";
    private static final String HELP_COMMAND_KEY = "ftd.telegram_helper.command.help";

    private static final List<String> COMMON_COMMAND_KEYS = Arrays.asList(
            START_COMMAND_KEY, MAIN_MENU_COMMAND_KEY, STOP_CHATTING_COMMAND_KEY,
            MONEY_COMMAND_KEY, HELP_COMMAND_KEY, VAACUMATOR_FAQ_COMMAND_KEY
    );

    private static final String FIRST_STEP_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.firstStep";
    private static final String SECOND_STEP_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.secondStep";
    private static final String THIRD_STEP_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.thirdStep";
    private static final String FOURTH_STEP_COMMAND_KEY = "ftd.telegram_helper.command.faq.vaacumator.fourthStep";

    private static final List<String> FAQ_STEP_COMMAND_KEYS = Arrays.asList(
            FIRST_STEP_COMMAND_KEY, SECOND_STEP_COMMAND_KEY,
            THIRD_STEP_COMMAND_KEY, FOURTH_STEP_COMMAND_KEY
    );

    private static final List<String> KNOWN_COMMANDS_KEYS = new ArrayList<>() {{
        addAll(COMMON_COMMAND_KEYS);
        addAll(FAQ_STEP_COMMAND_KEYS);
    }};

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

    public Command getVaacumatorFaqCommand() {
        return Command.create(messageBundle.loadMessage(VAACUMATOR_FAQ_COMMAND_KEY));
    }

    public Command getHelpCommand() {
        return Command.create(messageBundle.loadMessage(HELP_COMMAND_KEY));
    }

    public Command getMoneyCommand() {
        return Command.create(messageBundle.loadMessage(MONEY_COMMAND_KEY));
    }

    public List<Command> getVaacumatorFaqStepCommands() {
        return FAQ_STEP_COMMAND_KEYS
                .stream()
                .map(it -> Command.create(messageBundle.loadMessage(it)))
                .toList();
    }
    public Command getVaacumatorFaqStepCommand(int stepNumber) {
        if (stepNumber < 0 || stepNumber > FAQ_STEP_COMMAND_KEYS.size()) {
            throw new RuntimeException("Wrong step number has been detected [" + stepNumber + "]");
        }
        switch (stepNumber) {
            case 0 -> {
                throw new RuntimeException("Step number must be > 0");
            }
            case 1 -> {
                return getFirstFaqStepCommand();
            }
            case 2 -> {
                return getSecondFaqStepCommand();
            }
            case 3 -> {
                return getThirdFaqStepCommand();
            }
            case 4 -> {
                return getFourthFaqStepCommand();
            }
            default -> throw new RuntimeException("Wrong step number has been detected [" + stepNumber + "]");
        }
    }

    public Command getFirstFaqStepCommand() {
        return Command.create(messageBundle.loadMessage(FIRST_STEP_COMMAND_KEY));
    }

    public Command getSecondFaqStepCommand() {
        return Command.create(messageBundle.loadMessage(SECOND_STEP_COMMAND_KEY));
    }

    public Command getThirdFaqStepCommand() {
        return Command.create(messageBundle.loadMessage(THIRD_STEP_COMMAND_KEY));
    }

    public Command getFourthFaqStepCommand() {
        return Command.create(messageBundle.loadMessage(FOURTH_STEP_COMMAND_KEY));
    }

    public List<Command> getKnownCommands() {
        return KNOWN_COMMANDS_KEYS
                .stream()
                .map(it -> Command.create(messageBundle.loadMessage(it)))
                .toList();
    }
}


