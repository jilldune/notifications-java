package com.octalinc.notificationsjava.NotifierUtil;

public enum NotifyInput {
    TEXT_FIELD("text"),
    PASSWORD_FIELD("password"),
    TEXTAREA("textarea");

    private final String value;

    NotifyInput(String value) { this.value = value; }

    public String getValue() { return this.value; }

    public static NotifyInput fromString(String value) {
        for (NotifyInput input : NotifyInput.values()) {
            if (input.value.equalsIgnoreCase(value))
                return input;
        }

        throw new IllegalArgumentException("Invalid notification position:"+value);
    }
}
