package com.edubiz.notificationsjava.NotifierUtil;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotifyUtils {
    private ChangeListener<Number> widthListener;
    private ChangeListener<Number> heightListener;
    private ChangeListener<Boolean> fullScreenListener;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void timeOut(Runnable taskFunc, double durationInSeconds) {
        long durationInMillis = (long) (durationInSeconds * 1000); // Convert seconds to milliseconds
        scheduler.schedule(taskFunc, durationInMillis, TimeUnit.MILLISECONDS);
    }

    // Call this method to properly shut down the scheduler when application is closing
    public static void shutdownScheduler() {
        scheduler.shutdown();
    }

    public Map<String, Object> parsePosition(NotifyPos position, double width, double height, double sceneWidth, double sceneHeight) {
        // Define positioning
        double fromX;
        double fromY;
        double toX;
        double toY;
        double pad = 10.0;

        // Process the notification position
        String nPos = extractPosition(position);

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
                fromX = 0.0;
                fromY = 0.0;
                toX = (sceneWidth - width) / 2;
                toY = (sceneHeight - height) / 2;
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

        NotifyCoord coordinates = new NotifyCoord(fromX,fromY,toX,toY,pad,nPos);

        return coordinates.getCoordinates();
    }

    public String extractPosition(NotifyPos position) {
        // Process the notification position first
        String nPos = "";
        switch (position) {
            case TOP,TOP_LEFT,TOP_RIGHT,LEFT,RIGHT,BOTTOM,BOTTOM_LEFT,BOTTOM_RIGHT,CENTER ->  nPos = position.getPosition();
        }

        return nPos;
    }

    public Map<String, Double> getGeometry(Node node, Scene scene) {
        // create Map Object
        Map<String, Double> geoObj = new HashMap<>();

        // Define bounds
        double width, height, sceneWidth, sceneHeight;

        if (node instanceof Region region) {
            region.applyCss();
            region.layout();

            width = region.getPrefWidth();
            height = region.getPrefHeight();

            if (width == Region.USE_COMPUTED_SIZE || Double.isNaN(width)) width = region.getMaxWidth();

            if (height == Region.USE_COMPUTED_SIZE || Double.isNaN(height)) height = region.getMaxHeight();

            // check if with is less than 0
            if (width < 0) width = Math.max(width, Math.max(region.prefWidth(-1),region.getMinWidth()));
            if (height < 0) height = Math.max(height, Math.max(region.prefHeight(-1),region.getMinHeight()));
        }
        else {
            Bounds bounds = node.getBoundsInLocal();

            width = bounds.getWidth();
            height = bounds.getHeight();
        }

        // Get the scene bounds
        sceneWidth = scene.getWidth();
        sceneHeight = scene.getHeight();

        // Set Values
        geoObj.put("width", width);
        geoObj.put("height",height);
        geoObj.put("sceneWidth",sceneWidth);
        geoObj.put("sceneHeight",sceneHeight);

        return geoObj;
    }


    // Add listeners
    public void addNodeListeners(Stage stage, Node node, NotifyPos position, Double width, Double height) {
        // Get scene
        Scene scene = stage.getScene();

        // AddListeners to update positions
        // Width
        widthListener = (obs,oldVal,newV) -> {
            double updatedSceneWidth = newV.doubleValue();
            double updatedSceneHeight = scene.getHeight();

            Map<String, Object> updatedCoordinate = parsePosition(position,width,height,updatedSceneWidth,updatedSceneHeight);

            // Initial Position
            node.setLayoutX((double) updatedCoordinate.get("toX"));
            node.setLayoutY((double) updatedCoordinate.get("toY"));
        };
        scene.widthProperty().addListener(widthListener);

        // Height
        heightListener = (obs,oldVal,newV) -> {
            double updatedSceneWidth = scene.getWidth();
            double updatedSceneHeight = newV.doubleValue();

            Map<String, Object> updatedCoordinate = parsePosition(position,width,height,updatedSceneWidth, updatedSceneHeight);

            // Initial Position
            node.setLayoutX((double) updatedCoordinate.get("toX"));
            node.setLayoutY((double) updatedCoordinate.get("toY"));
        };
        scene.heightProperty().addListener(heightListener);

        // Full screen mode
        fullScreenListener = (obs,wasFullScreen,isFullScreen) -> {
            if (isFullScreen) {
                double fullScreenWidth = Screen.getPrimary().getBounds().getWidth();
                double fullScreenHeight = Screen.getPrimary().getBounds().getHeight();

                Map<String, Object> updatedCoordinate = parsePosition(position,width,height,fullScreenWidth,fullScreenHeight);

                // Initial Position
                node.setLayoutX((double) updatedCoordinate.get("toX"));
                node.setLayoutY((double) updatedCoordinate.get("toY"));
            }
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
