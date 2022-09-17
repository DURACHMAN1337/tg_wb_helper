package com.ftd.telegramhelper.util.command;


public final class Command {

    public static Command create(String value) {
        return new Command(value);
    }

    private final String message;

    private Command(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return getMessage().equals(command.getMessage());
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }
}
