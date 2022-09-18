package com.ftd.telegramhelper.util.state;

public enum UserStates {
    NEW("new"),
    CAN_SEND_MESSAGES("can_send_messages"),
    IN_MENU("in_menu"),
    READ_FAQ("read_faq");

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
