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

import java.util.Objects;

public class NotifyManager {
    private final AnchorPane root;
    private final Stage stage;

    public NotifyManager(Stage stage) {
        this.stage = stage;
        this.root = initRoot();
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

    private void initializeNotification(BaseNotifier baseNotifier) {
        baseNotifier.setRoot(root);
        Region layout = baseNotifier.getLayout();

        root.getChildren().clear();
        root.getChildren().add(layout);
    }
}
