package com.edubiz.notificationsjava.Notifications;

public enum NotificationPos {
    TOP("top"),
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    LEFT("left"),
    RIGHT("right"),
    BOTTOM("bottom"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right"),
    CENTER("center");

    private final String position;

    NotificationPos(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }


    public static NotificationPos fromString(String position) {
        for (NotificationPos pos : NotificationPos.values()) {
            if (pos.position.equalsIgnoreCase(position))
                return pos;
        }

        throw new IllegalArgumentException("Invalid notification position:"+position);
    }
}