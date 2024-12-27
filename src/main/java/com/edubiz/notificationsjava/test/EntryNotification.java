package com.edubiz.notificationsjava.test;

import com.edubiz.notificationsjava.Managers.NotifyManager;
import com.edubiz.notificationsjava.NotifierUtil.NotificationPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifierInputType;
import com.edubiz.notificationsjava.NotifierUtil.NotifierToastType;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.Notify.*;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class EntryNotification extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // create  the root
        AnchorPane root = new AnchorPane();
        root.setPrefSize(800,600);
        root.setStyle("-fx-background-color: red;");

        NotifyManager manager = new NotifyManager(stage);

        Button button = new Button("See MyNotifier");
        button.setCursor(Cursor.HAND);
        button.setOnAction(e -> {
//            ToastNotifier toast = manager.create(NotifyType.TOAST);
//            toast.create(NotifierToastType.SUCCESS,"This is a brave toast",NotificationPos.TOP,true,10.0);

//            PromptNotifier prompt = manager.create(NotifyType.PROMPT);
//            prompt.setHeader("Prompt")
//                    .setLabel("Hi there this is a prompt \nI am doing great")
//                    .autoClose(false)
//                    .setAnimation(true)
//                    .setPlaceholder("Hello placeholder")
//                    .create();

            Notifier notify = manager.create(NotifyType.NOTIFIER);
            notify.setHeader("Notification")
                    .setBody("I am a notification in the making please take it light.\nI am the new order")
                    .setPosition(NotificationPos.TOP_LEFT)
                    .setAnimation(true)
                    .autoClose(false)
                    .create();
        });

        root.getChildren().add(button);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MyNotifier Demo");


        // Shoe the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}