package com.octalinc.notificationsjava.core;

import com.octalinc.notificationsjava.utils.NotifyAnimation;
import com.octalinc.notificationsjava.utils.NotifyCoordinate;
import com.octalinc.notificationsjava.enums.NotifyPos;
import com.octalinc.notificationsjava.utils.NotifyUtils;
import com.octalinc.notificationsjava.enums.Lifecycle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class NotifyBase {

    protected final Stage stage;

    private final Region layout;

    private Node node;

    private AnchorPane root;

    private final NotifyUtils notifyUtils;

    private final NotifyAnimation animations;

    private final PauseTransition autoCloseTimer;

    // ---------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------

    private NotifyPos position = NotifyPos.TOP;

    private double animationDuration = 0.5;

    private double displayDuration = 0.5;

    private boolean animateNotification = true;

    private boolean autoClose = false;

    // ---------------------------------------------------------
    // LIFECYCLE
    // ---------------------------------------------------------

    private Lifecycle lifecycle = Lifecycle.NEW;

    private Consumer<NotifyBase> submitter;

    private boolean submitted = false;

    private Runnable onCloseRequest;

    private Runnable onClosed;

    private Runnable onDisplayFinished;

    // ---------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------

    public NotifyBase(Stage stage, Region layout) {

        this.stage = Objects.requireNonNull(stage);

        this.layout = Objects.requireNonNull(layout);

        this.notifyUtils = new NotifyUtils();

        this.animations = new NotifyAnimation();

        this.autoCloseTimer = new PauseTransition();

        this.autoCloseTimer.setOnFinished(event -> close());
    }

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------

    public Region getLayout() {
        return layout;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    // ---------------------------------------------------------
    // MANAGER INTEGRATION
    // ---------------------------------------------------------

    public void setSubmitter(Consumer<NotifyBase> submitter) {

        if (lifecycle != Lifecycle.NEW || submitted) {
            throw new IllegalStateException(
                    "Cannot change the submitter after submission."
            );
        }

        this.submitter = Objects.requireNonNull(submitter);
    }

    private void submit() {

        requireFxThread();

        if (submitted) {
            return;
        }

        if (submitter == null) {
            throw new IllegalStateException(
                    "Notification has not been connected to a NotifyManager."
            );
        }

        submitted = true;

        submitter.accept(this);
    }

    // ---------------------------------------------------------
    // NOTIFICATION CONFIGURATION
    // ---------------------------------------------------------

    protected void show(
            Node node,
            NotifyPos position,
            boolean animation,
            double animationDuration,
            boolean autoClose,
            double displayDuration
    ) {

        requireFxThread();

        if (lifecycle != Lifecycle.NEW || submitted) {
            throw new IllegalStateException(
                    "This notification has already been submitted."
            );
        }

        this.node = Objects.requireNonNull(node);

        this.position = Objects.requireNonNullElse(
                position,
                NotifyPos.TOP
        );

        if (!Double.isFinite(animationDuration) ||
                animationDuration < 0) {

            throw new IllegalArgumentException(
                    "Animation duration must be a finite, non-negative value."
            );
        }

        if (!Double.isFinite(displayDuration) ||
                displayDuration < 0) {

            throw new IllegalArgumentException(
                    "Display duration must be a finite, non-negative value."
            );
        }

        this.animateNotification = animation;

        this.animationDuration = animationDuration;

        this.autoClose = autoClose;

        this.displayDuration = displayDuration;

        submit();
    }

    // ---------------------------------------------------------
    // CALLBACKS
    // ---------------------------------------------------------

    public void setOnCloseRequest(Runnable callback) {
        this.onCloseRequest = callback;
    }

    public void setOnClosed(Runnable callback) {
        this.onClosed = callback;
    }

    private void callOnCloseRequest() {
        executeCallback(onCloseRequest);
    }

    private void callOnClosed() {
        executeCallback(onClosed);
    }

    private void executeCallback(Runnable callback) {

        if (callback == null) {
            return;
        }

        try {
            callback.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // ROOT MANAGEMENT
    // ---------------------------------------------------------

    public void setRoot(AnchorPane root) {
        this.root = Objects.requireNonNull(root);
    }

    // ---------------------------------------------------------
    // DISPLAY
    // ---------------------------------------------------------

    public void display(Runnable callback) {

        runOnFxThread(() -> {

            if (lifecycle != Lifecycle.NEW) {
                return;
            }

            lifecycle = Lifecycle.DISPLAYING;

            // Store the callback before validation.
            // Otherwise, a display failure could leave the manager queue stuck.
            this.onDisplayFinished = callback;

            if (node == null ||
                    root == null ||
                    stage.getScene() == null) {

                System.err.println(
                        "Cannot display notification: missing node, root or scene."
                );

                finishClose(null);

                return;
            }

            try {

                root.setManaged(true);

                root.setVisible(true);

                if (animateNotification) {

                    animationRoute();

                } else {

                    setInanimatePosition(node, position);

                    onEntranceFinished();
                }

            } catch (Exception e) {

                System.err.println(
                        "Notification display failed: " + e.getMessage()
                );

                e.printStackTrace();

                finishClose(null);
            }
        });
    }

    // ---------------------------------------------------------
    // ENTRANCE ANIMATION
    // ---------------------------------------------------------

    private void animationRoute() {

        animations.animate(
                node,
                position,
                stage,
                animationDuration,
                this::onEntranceFinished
        );
    }

    private void onEntranceFinished() {

        if (lifecycle != Lifecycle.DISPLAYING) {
            return;
        }

        lifecycle = Lifecycle.VISIBLE;

        if (autoClose && displayDuration > 0) {

            autoCloseTimer.setDuration(
                    Duration.seconds(displayDuration)
            );

            autoCloseTimer.playFromStart();
        }
    }

    // ---------------------------------------------------------
    // STATIC POSITIONING
    // ---------------------------------------------------------

    private void setInanimatePosition(
            Node node,
            NotifyPos position
    ) {

        if (node == null ||
                root == null ||
                stage.getScene() == null) {
            return;
        }

        Scene scene = stage.getScene();

        Map<String, Double> geometry =
                notifyUtils.getGeometry(node, scene);

        double width = geometry.get("width");

        double height = geometry.get("height");

        double sceneWidth = geometry.get("sceneWidth");

        double sceneHeight = geometry.get("sceneHeight");

        NotifyCoordinate coordinates = notifyUtils.parsePosition(
                position,
                width,
                height,
                sceneWidth,
                sceneHeight
        );

        node.setLayoutX(coordinates.toX());

        node.setLayoutY(coordinates.toY());

        notifyUtils.addNodeListeners(stage, node, position);
    }

    // ---------------------------------------------------------
    // CLOSE
    // ---------------------------------------------------------

    protected void close() {
        close(null);
    }

    protected void close(Runnable callback) {

        runOnFxThread(() -> {

            if (lifecycle == Lifecycle.CLOSING ||
                    lifecycle == Lifecycle.CLOSED) {
                return;
            }

            lifecycle = Lifecycle.CLOSING;

            autoCloseTimer.stop();

            closeInternal(callback);
        });
    }

    private void closeInternal(Runnable callback) {

        callOnCloseRequest();

        if (node == null || root == null) {

            finishClose(callback);

            return;
        }

        if (animateNotification) {

            try {

                animations.reverseTransition(
                        () -> finishClose(callback)
                );

            } catch (Exception e) {

                e.printStackTrace();

                finishClose(callback);
            }

        } else {

            finishClose(callback);
        }
    }

    // ---------------------------------------------------------
    // FINISH CLOSE
    // ---------------------------------------------------------

    private void finishClose(Runnable callback) {

        if (lifecycle == Lifecycle.CLOSED) {
            return;
        }

        lifecycle = Lifecycle.CLOSED;

        cleanupResources();

        callOnClosed();

        executeCallback(callback);

        executeCallback(onDisplayFinished);

        onDisplayFinished = null;
    }

    // ---------------------------------------------------------
    // RESOURCE CLEANUP
    // ---------------------------------------------------------

    private void cleanupResources() {

        autoCloseTimer.stop();

        try {

            if (root != null) {

                root.setVisible(false);

                root.setManaged(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notifyUtils.removeListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            animations.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // DISPOSAL
    // ---------------------------------------------------------

    public void dispose() {

        runOnFxThread(() -> {

            if (lifecycle == Lifecycle.CLOSED) {
                return;
            }

            lifecycle = Lifecycle.CLOSED;

            // Do not invoke user callbacks during manager disposal.
            onDisplayFinished = null;

            onCloseRequest = null;

            onClosed = null;

            cleanupResources();
        });
    }

    // ---------------------------------------------------------
    // THREAD MANAGEMENT
    // ---------------------------------------------------------

    private void requireFxThread() {

        if (!Platform.isFxApplicationThread()) {

            throw new IllegalStateException(
                    "Notifications must be configured on the JavaFX Application Thread."
            );
        }
    }

    private void runOnFxThread(Runnable task) {

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }
}