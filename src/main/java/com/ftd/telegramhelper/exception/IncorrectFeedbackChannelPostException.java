package com.ftd.telegramhelper.exception;

public class IncorrectFeedbackChannelPostException extends Exception {
    public IncorrectFeedbackChannelPostException() {
        super("Incorrect feedback channel post has been detected");
    }
}
