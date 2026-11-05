package com.octalinc.notificationsjava.NotifierUtil;

public record NotifyCoord(
        double fromX,
        double fromY,
        double toX,
        double toY,
        double pad,
        String position
) {

    public NotifyCoord {
        if (position == null || position.isBlank()) {
            position = "center";
        }
    }
}