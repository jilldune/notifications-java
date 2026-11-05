package com.octalinc.notificationsjava.NotifierUtil;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;

public class NotifyAnimation {

    private ChangeListener<Number> widthListener;
    private ChangeListener<Number> heightListener;
    private ChangeListener<Boolean> fullScreenListener;

    /*
     * References to the exact Scene and Stage
     * where listeners were registered.
     */
    private Scene attachedScene;
    private Stage attachedStage;

    private Node notification;
    private NotifyPos position;

    private double fromX;
    private double fromY;

    private double toX;
    private double toY;

    private double duration;

    private TranslateTransition translateTransition;
    private ScaleTransition scaleTransition;

    private boolean closing = false;
    private boolean entranceRunning = false;

    private Runnable entranceCompletion;

    // ---------------------------------------------------------
    // ENTRANCE ANIMATION
    // ---------------------------------------------------------

    /*
     * Entrance animation with completion callback.
     */
    public void animate(
            Node notification,
            NotifyPos position,
            Stage stage,
            double durationInSec,
            Runnable onFinished
    ) {

        if (notification == null ||
                position == null ||
                stage == null ||
                stage.getScene() == null) {

            throw new IllegalArgumentException(
                    "Notification, position and stage with a scene are required."
            );
        }

        stopTransitions();
        removeListeners();

        this.notification = notification;
        this.position = position;

        this.duration = durationInSec <= 0
                ? 0.5
                : durationInSec;

        this.closing = false;
        this.entranceRunning = true;
        this.entranceCompletion = onFinished;

        Scene scene = stage.getScene();

        this.attachedScene = scene;

        calculateCoordinates(scene);

        /*
         * Set final layout coordinates first.
         * Transitions animate relative to these coordinates.
         */
        notification.setLayoutX(toX);
        notification.setLayoutY(toY);

        if (isCenterPosition()) {

            notification.setScaleX(0);
            notification.setScaleY(0);

            scaleTransition = new ScaleTransition(
                    Duration.seconds(duration),
                    notification
            );

            scaleTransition.setFromX(0);
            scaleTransition.setFromY(0);

            scaleTransition.setToX(1);
            scaleTransition.setToY(1);

            scaleTransition.setOnFinished(event ->
                    finishEntrance()
            );

            scaleTransition.play();

        } else {

            double offsetX = fromX - toX;
            double offsetY = fromY - toY;

            notification.setTranslateX(offsetX);
            notification.setTranslateY(offsetY);

            translateTransition = new TranslateTransition(
                    Duration.seconds(duration),
                    notification
            );

            translateTransition.setFromX(offsetX);
            translateTransition.setFromY(offsetY);

            translateTransition.setToX(0);
            translateTransition.setToY(0);

            translateTransition.setOnFinished(event ->
                    finishEntrance()
            );

            translateTransition.play();
        }

        addNodeListeners(notification, position, stage, duration);
    }

    /*
     * Completes entrance exactly once.
     */
    private void finishEntrance() {

        if (!entranceRunning) {
            return;
        }

        entranceRunning = false;

        Runnable callback = entranceCompletion;
        entranceCompletion = null;

        if (callback != null) {
            callback.run();
        }
    }

    // ---------------------------------------------------------
    // COORDINATE CALCULATION
    // ---------------------------------------------------------

    private void calculateCoordinates() {

        if (notification == null ||
                position == null ||
                attachedScene == null) {
            return;
        }

        calculateCoordinates(attachedScene);
    }

    private void calculateCoordinates(Scene scene) {

        if (notification == null ||
                position == null ||
                scene == null) {
            return;
        }

        NotifyUtils notifyUtils = new NotifyUtils();

        Map<String, Double> geometry =
                notifyUtils.getGeometry(notification, scene);

        double width = geometry.get("width");
        double height = geometry.get("height");

        double sceneWidth = geometry.get("sceneWidth");
        double sceneHeight = geometry.get("sceneHeight");

        NotifyCoord coordinates =
                notifyUtils.parsePosition(
                        position,
                        width,
                        height,
                        sceneWidth,
                        sceneHeight
                );

        fromX = coordinates.fromX();
        fromY = coordinates.fromY();

        toX = coordinates.toX();
        toY = coordinates.toY();
    }

    private boolean isCenterPosition() {
        return position == NotifyPos.CENTER;
    }

    // ---------------------------------------------------------
    // EXIT ANIMATION
    // ---------------------------------------------------------

    public void reverseTransition(Runnable callback) {

        Runnable completion = callback != null
                ? callback
                : () -> {};

        if (notification == null) {
            completion.run();
            return;
        }

        if (closing) {
            return;
        }

        closing = true;

        /*
         * The notification is closing.
         * Do not allow the entrance callback to fire.
         */
        entranceRunning = false;
        entranceCompletion = null;

        stopTransitions();

        if (isCenterPosition()) {
            playScaleExit(completion);
        } else {
            playTranslateExit(completion);
        }
    }

    private void playTranslateExit(Runnable callback) {

        double offsetX = fromX - toX;
        double offsetY = fromY - toY;

        translateTransition = new TranslateTransition(
                Duration.seconds(duration),
                notification
        );

        translateTransition.setFromX(
                notification.getTranslateX()
        );

        translateTransition.setFromY(
                notification.getTranslateY()
        );

        translateTransition.setToX(offsetX);
        translateTransition.setToY(offsetY);

        translateTransition.setOnFinished(event -> {

            notification.setTranslateX(0);
            notification.setTranslateY(0);

            callback.run();
        });

        translateTransition.play();
    }

    private void playScaleExit(Runnable callback) {

        scaleTransition = new ScaleTransition(
                Duration.seconds(duration),
                notification
        );

        scaleTransition.setFromX(notification.getScaleX());
        scaleTransition.setFromY(notification.getScaleY());

        scaleTransition.setToX(0);
        scaleTransition.setToY(0);

        scaleTransition.setOnFinished(event ->
                callback.run()
        );

        scaleTransition.play();
    }

    // ---------------------------------------------------------
    // WINDOW RESIZING
    // ---------------------------------------------------------

    private void reposition() {

        if (notification == null ||
                position == null ||
                closing ||
                attachedScene == null) {
            return;
        }

        calculateCoordinates();

        notification.setLayoutX(toX);
        notification.setLayoutY(toY);

        /*
         * If resizing interrupts entrance, complete it
         * immediately rather than leaving the notification
         * stuck in DISPLAYING.
         */
        if (entranceRunning) {

            stopTransitions();

            notification.setTranslateX(0);
            notification.setTranslateY(0);

            notification.setScaleX(1);
            notification.setScaleY(1);

            finishEntrance();

            return;
        }

        /*
         * A settled notification only needs repositioning.
         * Do not stop unrelated transitions here.
         */
        notification.setTranslateX(0);
        notification.setTranslateY(0);
    }

    // ---------------------------------------------------------
    // LISTENER MANAGEMENT
    // ---------------------------------------------------------

    public void addNodeListeners(
            Node notification,
            NotifyPos position,
            Stage stage,
            Double duration
    ) {

        removeListeners();

        if (notification == null ||
                position == null ||
                stage == null ||
                stage.getScene() == null) {
            return;
        }

        this.notification = notification;
        this.position = position;

        this.attachedStage = stage;
        this.attachedScene = stage.getScene();

        /*
         * Scene width listener.
         */
        widthListener = (observable, oldValue, newValue) ->
                reposition();

        attachedScene.widthProperty()
                .addListener(widthListener);

        /*
         * Scene height listener.
         */
        heightListener = (observable, oldValue, newValue) ->
                reposition();

        attachedScene.heightProperty()
                .addListener(heightListener);

        /*
         * Full-screen listener.
         */
        fullScreenListener = (observable, wasFullScreen, isFullScreen) ->
                reposition();

        attachedStage.fullScreenProperty()
                .addListener(fullScreenListener);
    }

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

        if (attachedStage != null &&
                fullScreenListener != null) {

            attachedStage.fullScreenProperty()
                    .removeListener(fullScreenListener);

            fullScreenListener = null;
        }

        attachedScene = null;
        attachedStage = null;
    }

    // ---------------------------------------------------------
    // TRANSITION MANAGEMENT
    // ---------------------------------------------------------

    private void stopTransitions() {

        if (translateTransition != null) {
            translateTransition.stop();
            translateTransition = null;
        }

        if (scaleTransition != null) {
            scaleTransition.stop();
            scaleTransition = null;
        }
    }

    public void dispose() {

        stopTransitions();
        removeListeners();

        entranceRunning = false;
        entranceCompletion = null;

        notification = null;
        position = null;

        closing = false;
    }
}