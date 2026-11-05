package com.octalinc.notificationsjava.core;

import com.octalinc.notificationsjava.enums.NotifyType;
import com.octalinc.notificationsjava.components.Dialog;
import com.octalinc.notificationsjava.components.Notification;
import com.octalinc.notificationsjava.components.Prompt;
import com.octalinc.notificationsjava.components.Toast;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class NotifyManager {

    private final Stage stage;

    /*
     * Notification overlay.
     */
    private final AnchorPane root;

    /*
     * Container holding the application's original root
     * and the notification overlay.
     */
    private StackPane host;

    /*
     * Original application root.
     */
    private Parent applicationRoot;

    private Scene attachedScene;

    private ChangeListener<Parent> sceneRootListener;

    private ChangeListener<Scene> stageSceneListener;

    /*
     * Notification queue.
     */
    private final Queue<NotifyBase> notificationQueue =
            new ArrayDeque<>();

    private NotifyBase activeNotification;

    private boolean isNotificationDisplayed = false;

    private boolean dispatchScheduled = false;

    private boolean initialized = false;

    private boolean disposed = false;

    /*
     * Stylesheets.
     */
    private final List<String> defaultLibraryStyles =
            new ArrayList<>();

    private final List<String> customStyleSheets =
            new ArrayList<>();

    private int rootTransparencyLevel = 1;

    // ---------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------

    public NotifyManager(Stage stage) {

        requireFxThread();

        this.stage = Objects.requireNonNull(stage);

        this.root = new AnchorPane();

        initializeRoot();

        initializeStageListener();

        attachScene(stage.getScene());
    }

    // ---------------------------------------------------------
    // INITIALIZATION
    // ---------------------------------------------------------
    private void initializeRoot() {

        root.getStyleClass().add("root");

        root.setVisible(false);
        root.setManaged(false);

        /*
         * Do not let the empty overlay intercept events
         * intended for the application underneath it.
         */
        root.setPickOnBounds(false);

        root.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        initialized = true;

        initializeStyles();
    }

    private void initializeStyles() {

        String stylesheet = Objects.requireNonNull(
                getClass().getResource(
                        "/_notify-styles/notification-styles.css"
                ),
                "Notification stylesheet was not found."
        ).toExternalForm();

        defaultLibraryStyles.clear();

        defaultLibraryStyles.add(stylesheet);

        replaceStylesheets();
    }

    // ---------------------------------------------------------
    // SCENE MANAGEMENT
    // ---------------------------------------------------------

    private void initializeStageListener() {

        stageSceneListener = (observable, oldScene, newScene) -> {

            detachScene();

            attachScene(newScene);
        };

        stage.sceneProperty().addListener(stageSceneListener);
    }

    private void attachScene(Scene scene) {

        if (disposed || scene == null) {
            return;
        }

        attachedScene = scene;

        applicationRoot = scene.getRoot();

        /*
         * Create an overlay host.
         */
        host = new StackPane();

        host.setAlignment(Pos.TOP_LEFT);

        host.getChildren().addAll(
                applicationRoot,
                root
        );

        /*
         * Make the overlay fill the host.
         */
        StackPane.setAlignment(root, Pos.TOP_LEFT);

        scene.setRoot(host);

        /*
         * Listen for application root replacement.
         */
        sceneRootListener = (observable, oldRoot, newRoot) -> {

            if (newRoot == host || disposed || newRoot == null) {
                return;
            }

            replaceApplicationRoot(newRoot);
        };

        scene.rootProperty().addListener(sceneRootListener);

        /*
         * Keep the overlay dimensions synchronized.
         */
        root.prefWidthProperty().bind(host.widthProperty());
        root.prefHeightProperty().bind(host.heightProperty());

        /*
         * Reapply stylesheets.
         */
        replaceStylesheets();
    }

    private void replaceApplicationRoot(Parent newRoot) {

        if (host == null || newRoot == null) {
            return;
        }

        applicationRoot = newRoot;

        /*
         * The new application root becomes the background
         * of the notification host.
         */
        host.getChildren().setAll(
                applicationRoot,
                root
        );

        /*
         * Restore the host as the actual Scene root.
         *
         * The root listener recognizes host and therefore
         * does not recursively wrap it.
         */
        if (attachedScene.getRoot() != host) {
            attachedScene.setRoot(host);
        }
    }

    private void detachScene() {

        if (attachedScene == null) {
            return;
        }

        /*
         * Remove the root listener first.
         * This prevents our restoration from triggering
         * replaceApplicationRoot().
         */
        if (sceneRootListener != null) {

            attachedScene.rootProperty()
                    .removeListener(sceneRootListener);

            sceneRootListener = null;
        }

        /*
         * Unbind overlay dimensions.
         */
        root.prefWidthProperty().unbind();
        root.prefHeightProperty().unbind();

        /*
         * Keep references before clearing the fields.
         */
        Scene scene = attachedScene;
        StackPane currentHost = host;
        Parent currentApplicationRoot = applicationRoot;

        /*
         * Detach our nodes from the host.
         *
         * This is important because applicationRoot must
         * have no parent before it can become the Scene root.
         */
        if (currentHost != null) {

            currentHost.getChildren().remove(root);

            if (currentApplicationRoot != null) {
                currentHost.getChildren().remove(currentApplicationRoot);
            }
        }

        /*
         * Restore the application root only if our host
         * is still installed as the Scene root.
         *
         * If another part of the application has already
         * replaced the Scene root, leave that replacement intact.
         */
        if (scene.getRoot() == currentHost &&
                currentApplicationRoot != null) {

            scene.setRoot(currentApplicationRoot);
        }

        /*
         * Release references.
         */
        attachedScene = null;
        applicationRoot = null;
        host = null;
    }

    // ---------------------------------------------------------
    // STYLESHEETS
    // ---------------------------------------------------------

    public void addStyleSheet(String styleSheetURL) {

        runOnFxThread(() -> {

            if (disposed || styleSheetURL == null ||
                    styleSheetURL.isBlank()) {
                return;
            }

            if (!customStyleSheets.contains(styleSheetURL)) {
                customStyleSheets.add(styleSheetURL);
            }

            replaceStylesheets();
        });
    }

    private void replaceStylesheets() {

        if (!initialized) {
            return;
        }

        root.getStylesheets().setAll(
                defaultLibraryStyles
        );

        root.getStylesheets().addAll(
                customStyleSheets
        );
    }

    // ---------------------------------------------------------
    // ROOT CONFIGURATION
    // ---------------------------------------------------------

    private void setRootTransparencyLevel(int level) {

        root.getStyleClass().removeIf(
                style -> style.matches("level\\d+")
        );

        if (level > 0) {
            root.getStyleClass().add("level" + level);
        }
    }

    public void backgroundTransparency(int level) {

        if (level < 0) {
            throw new IllegalArgumentException(
                    "Transparency level cannot be negative."
            );
        }

        runOnFxThread(() -> {

            if (disposed) {
                return;
            }

            rootTransparencyLevel = level;

            setRootTransparencyLevel(level);
        });
    }

    public AnchorPane getRootPane() {
        return root;
    }

    // ---------------------------------------------------------
    // NOTIFICATION FACTORY
    // ---------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends NotifyBase> T create(NotifyType type) {

        requireFxThread();

        if (disposed) {
            throw new IllegalStateException(
                    "NotifyManager has already been disposed."
            );
        }

        NotifyBase notification = switch (Objects.requireNonNull(type)) {

            case TOAST -> new Toast(stage);

            case PROMPT -> new Prompt(stage);

            case DIALOG -> new Dialog(stage);

            case NOTIFICATION -> new Notification(stage);
        };

        /*
         * Supply the queue submission callback.
         *
         * The notification will invoke this only after
         * its component has finished building its UI.
         */
        notification.setSubmitter(this::initializeNotification);

        return (T) notification;
    }

    // ---------------------------------------------------------
    // QUEUE MANAGEMENT
    // ---------------------------------------------------------

    private void initializeNotification(NotifyBase notification) {

        if (disposed) {
            notification.dispose();
            return;
        }

        notificationQueue.offer(notification);

        scheduleNextNotification();
    }

    private void scheduleNextNotification() {

        if (disposed ||
                isNotificationDisplayed ||
                dispatchScheduled ||
                notificationQueue.isEmpty()) {
            return;
        }

        dispatchScheduled = true;

        Platform.runLater(() -> {

            dispatchScheduled = false;

            displayNextNotification();
        });
    }

    private void displayNextNotification() {

        if (disposed ||
                isNotificationDisplayed ||
                notificationQueue.isEmpty()) {
            return;
        }

        NotifyBase notifier = notificationQueue.poll();

        if (notifier == null) {
            return;
        }

        isNotificationDisplayed = true;

        activeNotification = notifier;

        try {

            /*
             * Apply background configuration.
             */
            setRootTransparencyLevel(rootTransparencyLevel);

            /*
             * Reset the overlay.
             */
            root.getChildren().clear();

            root.setManaged(false);
            root.setVisible(false);

            /*
             * Attach notification.
             */
            notifier.setRoot(root);

            Region layout = notifier.getLayout();

            root.getChildren().add(layout);

            /*
             * Display notification.
             */
            notifier.display(() -> notificationFinished(notifier));

        } catch (Exception e) {

            System.err.println(
                    "Failed to display notification: " + e.getMessage()
            );

            e.printStackTrace();

            notificationFinished(notifier);
        }
    }

    private void notificationFinished(NotifyBase notification) {

        runOnFxThread(() -> {

            /*
             * Ignore callbacks from notifications that
             * are no longer active.
             */
            if (activeNotification != notification) {
                return;
            }

            activeNotification = null;

            isNotificationDisplayed = false;

            root.getChildren().clear();

            root.setVisible(false);
            root.setManaged(false);

            scheduleNextNotification();
        });
    }

    // ---------------------------------------------------------
    // SHUTDOWN
    // ---------------------------------------------------------

    public void shutDown() {

        runOnFxThread(this::dispose);
    }

    public void dispose() {

        requireFxThread();

        if (disposed) {
            return;
        }

        disposed = true;

        // Dispose the currently displayed notification.
        if (activeNotification != null) {
            activeNotification.dispose();
            activeNotification = null;
        }

        // Dispose notifications waiting in the queue.
        while (!notificationQueue.isEmpty()) {

            NotifyBase notification = notificationQueue.poll();

            if (notification != null) {
                notification.dispose();
            }
        }

        dispatchScheduled = false;
        isNotificationDisplayed = false;

        // Remove Stage listener.
        if (stageSceneListener != null) {

            stage.sceneProperty()
                    .removeListener(stageSceneListener);

            stageSceneListener = null;
        }

        detachScene();

        root.getChildren().clear();

        root.setVisible(false);
        root.setManaged(false);

        customStyleSheets.clear();
        defaultLibraryStyles.clear();
    }

    // ---------------------------------------------------------
    // THREAD MANAGEMENT
    // ---------------------------------------------------------

    private void requireFxThread() {

        if (!Platform.isFxApplicationThread()) {

            throw new IllegalStateException(
                    "NotifyManager must be accessed from the JavaFX Application Thread."
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