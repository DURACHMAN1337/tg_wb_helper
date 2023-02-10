package com.ftd.telegramhelper.util.command;

public enum Command {
    START("/start"),
    INSTRUCTION("Как работает бот?"),
    ADMIN("/admin");

    private final String value;

    Command(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


