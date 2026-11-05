package com.octalinc.notificationsjava.utils;

import com.octalinc.notificationsjava.enums.NotifyPos;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotifyUtils {

    private ChangeListener<Number> widthListener;
    private ChangeListener<Number> heightListener;
    private ChangeListener<Boolean> fullScreenListener;

    private Scene attachedScene;
    private Stage attachedStage;

    private Node attachedNode;
    private NotifyPos attachedPosition;

    /*
     * Daemon thread prevents the scheduler from keeping
     * the application alive after the JavaFX application exits.
     */
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "Notify-Scheduler");
                thread.setDaemon(true);
                return thread;
            });

    // ---------------------------------------------------------
    // TIMEOUT
    // ---------------------------------------------------------

    public static void timeOut(
            Runnable taskFunc,
            double durationInSeconds
    ) {

        if (taskFunc == null) {
            return;
        }

        long durationInMillis = Math.max(
                0,
                (long) (durationInSeconds * 1000)
        );

        scheduler.schedule(
                () -> Platform.runLater(taskFunc),
                durationInMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public static void shutdownScheduler() {
        scheduler.shutdownNow();
    }

    // ---------------------------------------------------------
    // POSITION CALCULATION
    // ---------------------------------------------------------

    public NotifyCoordinate parsePosition(
            NotifyPos position,
            double width,
            double height,
            double sceneWidth,
            double sceneHeight
    ) {

        double fromX;
        double fromY;
        double toX;
        double toY;

        double pad = 10.0;

        String nPos = extractPosition(position);

        switch (nPos) {

            case "right" -> {
                double y = (sceneHeight - height) / 2;

                fromX = sceneWidth;
                toX = sceneWidth - width - pad;

                fromY = y;
                toY = y;
            }

            case "left" -> {
                double y = (sceneHeight - height) / 2;

                fromX = -width;
                toX = pad;

                fromY = y;
                toY = y;
            }

            case "bottom" -> {
                double x = (sceneWidth - width) / 2;

                fromY = sceneHeight;
                toY = sceneHeight - height - pad;

                fromX = x;
                toX = x;
            }

            case "center" -> {

                fromX = 0;
                fromY = 0;

                toX = (sceneWidth - width) / 2;
                toY = (sceneHeight - height) / 2;
            }

            case "top-left" -> {

                fromX = -width;
                fromY = -height;

                toX = pad;
                toY = pad;
            }

            case "top-right" -> {

                fromX = sceneWidth;
                fromY = -height;

                toX = sceneWidth - width - pad;
                toY = pad;
            }

            case "bottom-left" -> {

                fromX = -width;
                fromY = sceneHeight;

                toX = pad;
                toY = sceneHeight - height - pad;
            }

            case "bottom-right" -> {

                fromX = sceneWidth;
                fromY = sceneHeight;

                toX = sceneWidth - width - pad;
                toY = sceneHeight - height - pad;
            }

            default -> {

                double x = (sceneWidth - width) / 2;

                fromY = -height;
                fromX = x;

                toY = pad;
                toX = x;
            }
        }

        return new NotifyCoordinate(
                fromX,
                fromY,
                toX,
                toY,
                pad,
                nPos
        );
    }

    public String extractPosition(NotifyPos position) {

        return Objects.requireNonNullElse(position, NotifyPos.TOP).getPosition();

    }

    // ---------------------------------------------------------
    // GEOMETRY
    // ---------------------------------------------------------

    public Map<String, Double> getGeometry(
            Node node,
            Scene scene
    ) {

        if (node == null || scene == null) {
            throw new IllegalArgumentException(
                    "Node and Scene cannot be null."
            );
        }

        /*
         * CSS and layout must be applied before calculating
         * dimensions.
         */
        node.applyCss();
        node.autosize();
        // node.layout();

        double width;
        double height;

        if (node instanceof Region region) {

            width = resolveDimension(
                    region,
                    true
            );

            height = resolveDimension(
                    region,
                    false
            );

        } else {

            Bounds bounds = node.getLayoutBounds();

            width = bounds.getWidth();
            height = bounds.getHeight();
        }

        Map<String, Double> geometry = new HashMap<>();

        geometry.put("width", width);
        geometry.put("height", height);

        geometry.put("sceneWidth", scene.getWidth());
        geometry.put("sceneHeight", scene.getHeight());

        return geometry;
    }

    private double resolveDimension(
            Region region,
            boolean horizontal
    ) {

        double layoutSize = horizontal
                ? region.getLayoutBounds().getWidth()
                : region.getLayoutBounds().getHeight();

        double prefSize = horizontal
                ? region.prefWidth(-1)
                : region.prefHeight(-1);

        double minSize = horizontal
                ? region.minWidth(-1)
                : region.minHeight(-1);

        double maxSize = horizontal
                ? region.maxWidth(-1)
                : region.maxHeight(-1);

        /*
         * Prefer the computed preferred size.
         */
        double size = prefSize;

        /*
         * Fall back to the actual layout bounds if
         * preferred size is unavailable.
         */
        if (!isValidDimension(size)) {
            size = layoutSize;
        }

        /*
         * Last fallback.
         */
        if (!isValidDimension(size)) {
            size = minSize;
        }

        /*
         * Prevent invalid or excessively large dimensions.
         */
        size = Math.max(minSize, size);

        if (isValidDimension(maxSize)) {
            size = Math.min(maxSize, size);
        }

        return Math.max(0, size);
    }

    private boolean isValidDimension(double value) {

        return Double.isFinite(value) && value >= 0;
    }

    // ---------------------------------------------------------
    // RESPONSIVE POSITIONING
    // ---------------------------------------------------------

    public void addNodeListeners(
            Stage stage,
            Node node,
            NotifyPos position
    ) {

        removeListeners();

        if (stage == null || stage.getScene() == null || node == null) {
            return;
        }

        this.attachedStage = stage;
        this.attachedScene = stage.getScene();

        this.attachedNode = node;
        this.attachedPosition = position;

        /*
         * Scene width changes.
         */
        widthListener = (observable, oldValue, newValue) ->
                reposition();

        attachedScene.widthProperty().addListener(widthListener);

        /*
         * Scene height changes.
         */
        heightListener = (observable, oldValue, newValue) ->
                reposition();

        attachedScene.heightProperty().addListener(heightListener);

        /*
         * Fullscreen changes.
         * Scene dimensions remain our source of truth.
         */
        fullScreenListener = (observable, oldValue, newValue) ->
                Platform.runLater(this::reposition);

        attachedStage.fullScreenProperty()
                .addListener(fullScreenListener);
    }

    private void reposition() {

        if (attachedScene == null ||
                attachedNode == null ||
                attachedPosition == null) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::reposition);
            return;
        }

        Map<String, Double> geometry =
                getGeometry(attachedNode, attachedScene);

        NotifyCoordinate coordinates =
                parsePosition(
                        attachedPosition,
                        geometry.get("width"),
                        geometry.get("height"),
                        attachedScene.getWidth(),
                        attachedScene.getHeight()
                );

        attachedNode.setLayoutX(coordinates.toX());

        attachedNode.setLayoutY(coordinates.toY());
    }

    // ---------------------------------------------------------
    // LISTENER CLEANUP
    // ---------------------------------------------------------

    public void removeListeners() {

        if (attachedScene != null) {

            if (widthListener != null) {

                attachedScene.widthProperty()
                        .removeListener(widthListener);

                widthListener = null;
            }

            if (heightListener != null) {

                attachedScene.heightProperty()
                        .removeListener(heightListener);

                heightListener = null;
            }
        }

        if (attachedStage != null && fullScreenListener != null) {

            attachedStage.fullScreenProperty()
                    .removeListener(fullScreenListener);

            fullScreenListener = null;
        }

        attachedScene = null;
        attachedStage = null;

        attachedNode = null;
        attachedPosition = null;
    }
}