package com.ftd.telegramhelper.util.state;

public enum UserStates {
    NEW("new"),
    CAN_SEND_MESSAGES("can_send_messages"),
    IN_PROGRESS("in_progress"),
    READ_FAQ_VAACUMATOR("read_faq_vaacumator");

    private String value;

    UserStates(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserStates{" +
                "value='" + value + '\'' +
                '}';
    }
}
