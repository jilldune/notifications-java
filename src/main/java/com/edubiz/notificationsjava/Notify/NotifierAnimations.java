package com.edubiz.notificationsjava.Notify;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class NotifierAnimations {
    public void animate(Node notification, NotificationPos position, Scene scene,double durationInSec) {

        double duration = durationInSec == 0 ? durationInSec : .5;

        // Create the translation
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration),notification);

        // Define bounds
        double width, height, sceneWidth, sceneHeight;

        if (notification instanceof Region region) {
            region.applyCss();
            region.layout();

            width = region.prefWidth(-1);
            height = region.prefHeight(-1);
        } else {
            Bounds bounds = notification.getBoundsInLocal();

            width = bounds.getWidth();
            height = bounds.getHeight();
        }

        // Get the scene bounds
        sceneWidth = scene.getWidth();
        sceneHeight = scene.getHeight();

        // Define positioning
        double fromX;
        double fromY;
        double toX;
        double toY;
        double pad = 10.0;

        // Get position
        // Process the notification position
        String nPos = "";
        switch (position) {
            case TOP,TOP_LEFT,TOP_RIGHT,LEFT,RIGHT,BOTTOM,BOTTOM_LEFT,BOTTOM_RIGHT,CENTER ->  nPos = position.getPosition();
        }

        //Set the positions
        switch (nPos) {
            case "right" -> {
                double y = (sceneHeight - height) / 2;

                fromX = sceneWidth;
                toX = (sceneWidth - width) - pad;
                fromY = y;
                toY = y;
            }
            case "left" -> {
                double y = (sceneHeight - height) / 2;

                fromX = -width;
                toX = 0 + pad;
                fromY = y;
                toY = y;
            }
            case "bottom" -> {
                double x = (sceneWidth - width) / 2;

                fromY = sceneHeight;
                toY = (sceneHeight - height) - pad;
                fromX = x;
                toX = x;
            }
            case "center" -> {
                toX = (sceneWidth - width) / 2;
                toY = (sceneWidth - width) / 2;

                popIn(notification,toX,toY,duration);
                return;
            }
            case "top-left" -> {
                fromX = -width;
                fromY = -height;
                toX = 0 + pad;
                toY = 0 + pad;
            }
            case "top-right" -> {
                fromX = sceneWidth;
                fromY = -height;
                toX = (sceneWidth - width) - pad;
                toY = 0 + pad;
            }
            case "bottom-left" -> {
                fromX = -width;
                fromY = sceneHeight;
                toX = 0 + pad;
                toY = (sceneHeight - height) - pad;
            }
            case "bottom-right" -> {
                fromX = sceneWidth;
                fromY = sceneHeight;
                toX = (sceneWidth - width) - pad;
                toY = (sceneHeight - height) - pad;
            }
            default -> {
                double x = (sceneWidth - width) / 2;

                fromY = -height;
                fromX = x;
                toY = 0 + pad;
                toX = x;
            }
        }

        // Set up animation
        translateTransition.setFromX(fromX);
        translateTransition.setFromY(fromY);
        translateTransition.setToX(toX);
        translateTransition.setToY(toY);

        translateTransition.play();
    }

    private void popIn(Node notification,double toX,double toY,double duration) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(duration),notification);
        scale.setFromX(0);
        scale.setFromY(0);
        scale.setToX(1);
        scale.setToY(1);

        // set notification in the center
        notification.setLayoutX(toX);
        notification.setLayoutY(toY);

        // start animation
        scale.play();
    }
}
