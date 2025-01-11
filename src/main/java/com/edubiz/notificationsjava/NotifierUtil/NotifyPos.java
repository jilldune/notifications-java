package com.edubiz.notificationsjava.NotifierUtil;

public enum NotifyPos {
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

    NotifyPos(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }


    public static NotifyPos fromString(String position) {
        for (NotifyPos pos : NotifyPos.values()) {
            if (pos.position.equalsIgnoreCase(position))
                return pos;
        }

        throw new IllegalArgumentException("Invalid notification position:"+position);
    }
}
