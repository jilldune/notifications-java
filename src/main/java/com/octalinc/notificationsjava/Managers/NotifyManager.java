package com.octalinc.notificationsjava.Managers;

import com.octalinc.notificationsjava.NotifierUtil.NotifyUtils;
import com.octalinc.notificationsjava.Notify.Dialog;
import com.octalinc.notificationsjava.Notify.Notification;
import com.octalinc.notificationsjava.NotifierUtil.NotifyType;
import com.octalinc.notificationsjava.Notify.Prompt;
import com.octalinc.notificationsjava.Notify.Toast;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.*;

public class NotifyManager {
    private final AnchorPane root;
    private final Stage stage;
    private final Queue<NotifyBase> notificationQueue = new LinkedList<>();
    private boolean isNotificationDisplayed = false;
    private final List<String> defaultLibraryStyles = new ArrayList<>(); // Default library styles
    private final List<String> customStyleSheets = new ArrayList<>(); // User-added custom stylesheets
    private int rootTransparencyLevel = 1;


    public NotifyManager(Stage stage) {
        this.stage = stage;
        this.root = initRoot();
    }

    /***
     * Creates and initializes the root of the notifier on which all alerts/notification is drawn
     * with all default styles and add to the window/stage of the calling window
     *
     * @return root[AnchorPane]
     */
    private AnchorPane initRoot() {
        AnchorPane root = new AnchorPane();

        Platform.runLater(()->{
            initStyles();
            root.getStyleClass().add("root");
            setRootTransparencyLevel(0);
            root.setVisible(false);

            Scene scene = this.stage.getScene();
            Parent userRoot = scene.getRoot();

            if(! ((Pane) userRoot).getChildren().contains(root)) {
                ((Pane) userRoot).getChildren().add(root);

                // bind to a scene
                root.prefWidthProperty().bind(scene.widthProperty());
                root.prefHeightProperty().bind(scene.heightProperty());
            }
        });

        return root;
    }

    private void setRootTransparencyLevel(int level) {
        this.root.getStyleClass().clear();
        this.root.getStyleClass().add("root");

        if (level != 0)
            this.root.getStyleClass().add("level" + level);
    }

    /**
     * Initializes the default library stylesheets.
     */
    private void initStyles() {
        // Add your default library styles here
        defaultLibraryStyles.add(Objects.requireNonNull(getClass().getResource("/_notify-styles/notification-styles.css")).toExternalForm());
        replaceStylesheets(); // Apply the default styles initially
    }

    /// Ensures that library styles remain in place and custom styles are applied last.
    /**
     * Adds a custom stylesheet provided by the user.
     *
     * @param styleSheetURL the URL of the custom stylesheet to add.
    */
    public void addStyleSheet(String styleSheetURL) {
        if (styleSheetURL != null && !styleSheetURL.trim().isEmpty()) {
            this.customStyleSheets.add(styleSheetURL); // Maintain a list of user-added stylesheets
            replaceStylesheets();
        }
    }

    /**
     * Clears and re-applies all stylesheets to the root pane.
     * Ensures library styles are applied first, followed by custom styles.
    */
    private void replaceStylesheets() {
        root.getStylesheets().clear();
        root.getStylesheets().addAll(defaultLibraryStyles);
        root.getStylesheets().addAll(customStyleSheets);
    }

    /**
     * Exposes the root pane for adding to the scene.
     *
     * @return the root AnchorPane.
    */
    public AnchorPane getRootPane() { return root; }

    public void backgroundTransparency(int level) {
        this.rootTransparencyLevel = level;
    }

    public void shutDown() {
        NotifyUtils.shutdownScheduler();
    }

    @SuppressWarnings("unchecked")
    public <T extends NotifyBase>T create(NotifyType type) {
        NotifyBase notification = switch (type) {
            case TOAST -> new Toast(stage);
            case PROMPT -> new Prompt(stage);
            case DIALOG -> new Dialog(stage);
            case NOTIFICATION -> new Notification(stage);
        };

        initializeNotification(notification);

        return (T) notification;
    }

    private synchronized void displayNextNotification() {
        if (!notificationQueue.isEmpty()) {
            Platform.runLater(()->{
                try {
                    NotifyBase notifier = notificationQueue.poll();
                    isNotificationDisplayed = true;

                    // set background transparency
                    this.setRootTransparencyLevel(this.rootTransparencyLevel);

                    root.setManaged(false);
                    root.setVisible(false);

                    // Add the root to the notifier class
                    assert notifier != null;
                    notifier.setRoot(root);

                    // Get the layout region
                    Region layout = notifier.getLayout();

                    // clear the root component
                    root.getChildren().clear();

                    // Add the layout to the root
                    root.getChildren().add(layout);

                    notifier.display(() -> {
                        isNotificationDisplayed = false;
                        this.displayNextNotification();
                    });
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.getCause();
                }
            });
        }
    }

    private synchronized void initializeNotification(NotifyBase notifyBase) {
        notificationQueue.add(notifyBase);

        if (!isNotificationDisplayed) {
            displayNextNotification();
        }
    }
}
