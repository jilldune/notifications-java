package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.NotifierUtil.Helper;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;

public class NotifierAnimations {
    private Map<String, Double> coordinates;
    private ChangeListener<Number> widthListener;
    private ChangeListener<Number> heightListener;
    private ChangeListener<Boolean> fullScreenListener;

    public void animate(Node notification, NotificationPos position, Stage stage,double durationInSec) {
        // Get scene
        Scene scene = stage.getScene();

        // Get duration
        double duration = durationInSec == 0 ? durationInSec : .5;

        // Create the translation
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration),notification);

        // Define bounds
        double width, height, sceneWidth, sceneHeight;

        // Helper Class
        Helper helper = new Helper();

        // get geometry
        Map<String, Double> geoMetry = helper.getGeometry(notification,scene);

        // Set bounds
        width = geoMetry.get("width");
        height = geoMetry.get("height");

        // Get the scene bounds
        sceneWidth = geoMetry.get("sceneWidth");
        sceneHeight = geoMetry.get("sceneHeight");

        // Define positioning
        Map<String, Object> coordinate = helper.parsePosition(position,width,height,sceneWidth,sceneHeight);
        double fromX = (double) coordinate.get("fromX");
        double fromY = (double) coordinate.get("fromY");
        double toX = (double) coordinate.get("toX");
        double toY = (double) coordinate.get("toY");
        String pos = (String) coordinate.get("position");

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

    public void addNodeListeners(Node notification,NotificationPos position,Stage stage,Double duration) {
        // Remove old listeners
        this.removeListeners(stage);

        // Get the new scene
        Scene scene = stage.getScene();

        // Width
        widthListener = (obs,oldVal,newV) -> {
            this.animate(notification,position,stage,duration);
        };
        scene.widthProperty().addListener(widthListener);

        // Height
        heightListener = (obs,oldVal,newV) -> {
            this.animate(notification,position,stage,duration);
        };
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
