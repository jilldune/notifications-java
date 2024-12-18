package com.edubiz.notificationsjava.Notifications;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public abstract class BaseNotifications {
    protected Stage stage;
    protected StackPane root;

    public BaseNotifications() {
        stage = new Stage(StageStyle.TRANSPARENT);
        root = new StackPane();
//        root.setStyle("-fx-background-radius: 5; -fx-background-color: rgba(0,0,0,0);");

        Scene scene = new Scene(root);
        scene.setFill(null);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/notification-styles.css")).toExternalForm());
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
