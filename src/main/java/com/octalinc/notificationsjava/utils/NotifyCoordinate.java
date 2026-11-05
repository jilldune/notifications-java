package com.octalinc.notificationsjava.utils;

public record NotifyCoordinate(
        double fromX,
        double fromY,
        double toX,
        double toY,
        double pad,
        String position
) {

    public NotifyCoordinate {
        if (position == null || position.isBlank()) {
            position = "center";
        }
    }
}