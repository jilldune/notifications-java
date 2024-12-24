package com.edubiz.notificationsjava.NotifierUtil;

public enum NotifierInputType {
    TEXT("text"),
    TEXTAREA("textarea");

    private final String value;

    NotifierInputType(String value) { this.value = value; }

    public String getValue() { return this.value; }

    public static NotifierInputType fromString(String value) {
        for (NotifierInputType input : NotifierInputType.values()) {
            if (input.value.equalsIgnoreCase(value))
                return input;
        }

        throw new IllegalArgumentException("Invalid notification position:"+value);
    }
}
