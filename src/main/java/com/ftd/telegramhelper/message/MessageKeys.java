package com.ftd.telegramhelper.message;

import java.util.Arrays;
import java.util.List;

public abstract class MessageKeys {
    public static final String WELCOME_MESSAGE_KEY = "ftd.telegram_helper.message.welcome";
    public static final String SUCCESS_MESSAGE_KEY = "ftd.telegram_helper.message.success";
    public static final String FAILURE_MESSAGE_KEY = "ftd.telegram_helper.message.failure";
    public static final String HELP_MESSAGE_KEY = "ftd.telegram_helper.message.help";
    public static final String DETAILS_MESSAGE_KEY = "ftd.telegram_helper.message.details";
    public static final String ERROR_MESSAGE_KEY = "ftd.telegram_helper.message.error";

    public abstract class CommandsMessageKeys {
        public static final String START_COMMAND = "ftd.telegram_helper.command.start";
        public static final List<String> KNOWN_COMMAND_MESSAGE_KEY = Arrays.asList(
                START_COMMAND
        );
    }
}
