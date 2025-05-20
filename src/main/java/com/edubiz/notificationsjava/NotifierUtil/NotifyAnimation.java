package com.edubiz.notificationsjava.NotifierUtil;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;

import static com.edubiz.notificationsjava.NotifierUtil.NotifyUtils.timeOut;

public class NotifyAnimation {
    private ChangeListener<Number> widthListener;
    private ChangeListener<Number> heightListener;
    private ChangeListener<Boolean> fullScreenListener;
    private double fromX;
    private double fromY;
    private double toX;
    private double toY;
    private double duration;
    private Node notification;
    private String pos;

    public void animate(Node notification, NotifyPos position, Stage stage, double durationInSec) {
        // Get scene
        Scene scene = stage.getScene();

        // Get duration
        duration = durationInSec <= 0 ? 0.5 : durationInSec;

        // Create the translation
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), notification);
        this.notification = notification;

        // Define bounds
        double width, height, sceneWidth, sceneHeight;

        // NotifyUtils Class
        NotifyUtils notifyUtils = new NotifyUtils();

        // get geometry
        Map<String, Double> geometry = notifyUtils.getGeometry(notification,scene);

        // Set bounds
        width = geometry.get("width");
        height = geometry.get("height");

        // Get the scene bounds
        sceneWidth = geometry.get("sceneWidth");
        sceneHeight = geometry.get("sceneHeight");

        // Define positioning
        Map<String, Object> coordinate = notifyUtils.parsePosition(position,width,height,sceneWidth,sceneHeight);
        fromX = (double) coordinate.get("fromX");
        fromY = (double) coordinate.get("fromY");
        toX = (double) coordinate.get("toX");
        toY = (double) coordinate.get("toY");
        pos = (String) coordinate.get("position");

        if (pos.equals("center")) {
            popIn(notification, toX, toY, duration);
            this.addNodeListeners(notification,position,stage,duration);
            return;
        }

        // Set up animation
        translateTransition.setFromX(fromX);
        translateTransition.setFromY(fromY);
        translateTransition.setToX(toX);
        translateTransition.setToY(toY);

        translateTransition.play();

        // AddListeners to update positions
        this.addNodeListeners(notification,position,stage,duration);
    }

    // Reverses the transition
    public void reverseTransition(Runnable callback) {
        if (fromX != 0.0 || fromY != 0.0 || toX != 0.0 || toY != 0.0) {
            timeOut(() -> {
                if (pos.equals("center")) {
                    popOut(notification, toX, toY, duration,callback);
                    return;
                }

                TranslateTransition reverse = new TranslateTransition(Duration.seconds(duration),notification);
                reverse.setFromX(toX);
                reverse.setFromY(toY);
                reverse.setToX(fromX);
                reverse.setToY(fromY);
                reverse.setOnFinished(actionEvent -> callback.run());

                reverse.play();
            },.5);
        }
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


    private void popOut(Node notification,double toX,double toY,double duration,Runnable callback) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(duration),notification);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(0);
        scale.setToY(0);
        scale.setOnFinished(actionEvent -> callback.run());

        // set notification in the center
        notification.setLayoutX(toX);
        notification.setLayoutY(toY);

        // start animation
        scale.play();
    }

    public void addNodeListeners(Node notification, NotifyPos position, Stage stage, Double duration) {
        // Remove old listeners
        this.removeListeners(stage);

        // Get the new scene
        Scene scene = stage.getScene();

        // Width
        widthListener = (obs,oldVal,newV) -> this.animate(notification,position,stage,duration);
        scene.widthProperty().addListener(widthListener);

        // Height
        heightListener = (obs,oldVal,newV) -> this.animate(notification,position,stage,duration);
        scene.heightProperty().addListener(heightListener);

        // Full screen mode
        fullScreenListener = (obs,wasFullScreen,isFullScreen) -> {
            if (isFullScreen) this.animate(notification,position,stage,duration);
        };
        stage.fullScreenProperty().addListener(fullScreenListener);
    }

    public void removeListeners(Stage stage) {
        Scene scene = stage.getScene();

        if (widthListener != null) {
            scene.widthProperty().removeListener(widthListener);
            widthListener = null;
        }
        if (heightListener != null) {
            scene.heightProperty().removeListener(heightListener);
            heightListener = null;
        }
        if (fullScreenListener != null) {
            stage.fullScreenProperty().removeListener(fullScreenListener);
        }
    }
}
