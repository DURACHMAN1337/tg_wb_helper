package com.ftd.telegramhelper.util.command;

public enum Command {
    START("/start"),
    INSTRUCTION("Как работает бот?"),

    TAKE_RUBLES("Получить 150 рублей"),

    FAQ("Как пользоваться вакууматором"),

    HELP("Связаться с поддержкой");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


