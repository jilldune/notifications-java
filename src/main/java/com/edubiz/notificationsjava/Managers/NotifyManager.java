package com.edubiz.notificationsjava.Managers;

import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.Notify.Dialog;
import com.edubiz.notificationsjava.Notify.Notification;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.Notify.Prompt;
import com.edubiz.notificationsjava.Notify.Toast;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class NotifyManager {
    private final AnchorPane root;
    private final Stage stage;
    private final Queue<NotifyBase> notificationQueue = new LinkedList<>();
    private boolean isNotificationDisplayed = false;

    public NotifyManager(Stage stage) {
        this.stage = stage;
        this.root = initRoot();
    }

    private AnchorPane initRoot() {
        AnchorPane root = new AnchorPane();

        Platform.runLater(()->{
            addStyleSheet(Objects.requireNonNull(getClass().getResource("/_notify-styles/notification-styles.css")).toExternalForm());
            root.getStyleClass().add("root");
            root.setVisible(false);

            Scene scene = this.stage.getScene();
            Parent userRoot = scene.getRoot();

            if(! ((Pane) userRoot).getChildren().contains(root)) {
                ((Pane) userRoot).getChildren().add(root);

                // bind to scene
                root.prefWidthProperty().bind(scene.widthProperty());
                root.prefHeightProperty().bind(scene.heightProperty());
            }
        });

        return root;
    }

    public void addStyleSheet(String styleSheetPath) {
        if (!root.getStylesheets().contains(styleSheetPath))
            replaceStylesheets(styleSheetPath);
    }

    public void replaceStylesheets(String customSheet) {
        root.getStylesheets().clear();
        root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/_notify-styles/notification-styles.css")).toExternalForm());
        root.getStylesheets().add(customSheet);
    }

    public AnchorPane getRootPane() {
        return root;
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
