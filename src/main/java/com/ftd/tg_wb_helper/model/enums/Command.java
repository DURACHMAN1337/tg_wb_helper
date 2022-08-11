package com.ftd.tg_wb_helper.model.enums;

public enum Command {
    START("/start");

    private final String value;

    Command(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


