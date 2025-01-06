package com.edubiz.notificationsjava.Managers;

import com.edubiz.notificationsjava.Notify.Notifier;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.Notify.PromptNotifier;
import com.edubiz.notificationsjava.Notify.ToastNotifier;
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
    private final Queue<BaseNotifier> notificationQueue = new LinkedList<>();
    private boolean isNotificationDisplayed = false;

    public NotifyManager(Stage stage) {
        this.stage = stage;
        this.root = initRoot();

        System.out.println("Initialised main root: " + this.root);
    }

    private AnchorPane initRoot() {
        AnchorPane root = new AnchorPane();

        Platform.runLater(()->{
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/_notify-styles/notification-styles.css")).toExternalForm());
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
            root.getStylesheets().add(styleSheetPath);
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseNotifier>T create(NotifyType type) {
        BaseNotifier notification = switch (type) {
            case TOAST -> new ToastNotifier(stage);
            case PROMPT -> new PromptNotifier(stage);
            case NOTIFIER -> new Notifier(stage);
        };

        initializeNotification(notification);

        return (T) notification;
    }

    private synchronized void displayNextNotification() {
        System.out.println("Queue empty before call: " + notificationQueue.isEmpty());
        if (!notificationQueue.isEmpty()) {
            BaseNotifier notifier = notificationQueue.poll();
            isNotificationDisplayed = true;

            root.setManaged(false);
            root.setVisible(false);

            System.out.println("Empty Queue: " + notificationQueue.isEmpty());
            System.out.println("Root managed: " + root.isManaged());
            System.out.println("Root Visible: " + root.isVisible());
            System.out.println("Main root: " + this.root);

            notifier.setRoot(root);
            Region layout = notifier.getLayout();
            System.out.println("Layout: " + layout);

//            root.getChildren().clear();
            if (root.getChildren().size() == 1)
                root.getChildren().removeFirst();

            root.getChildren().add(layout);
            System.out.println("Root Children: " + root.getChildren().size());


            notifier.display(() -> {
                isNotificationDisplayed = false;
                System.out.println("callback");
                this.displayNextNotification();
            });
        }
    }

    private synchronized void initializeNotification(BaseNotifier baseNotifier) {
        notificationQueue.add(baseNotifier);

        if (!isNotificationDisplayed) {
            displayNextNotification();
        }
    }
}
