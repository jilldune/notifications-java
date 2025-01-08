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

        stage.setOnCloseRequest(ev -> {
            manager.shutDown();
        });

        Button button = new Button("See MyNotifier");
        button.setCursor(Cursor.HAND);
        button.setOnAction(e -> {
//            ToastNotifier toast = manager.create(NotifyType.TOAST);
//            toast.create(NotifierToastType.SUCCESS,"This is a brave toast",NotificationPos.TOP,true,10.0);

//
            // Prompt
            PromptNotifier prompt = manager.create(NotifyType.PROMPT);
            prompt.setHeader("Prompt")
                    .setLabel("Hi there this is a prompt \nI am doing great")
                    .autoClose(false)
                    .setAnimation(true)
                    .setPlaceholder("Hello placeholder")
                    .setButton("cancel",()->{})
                    .setButton("save",(text)->{
//                        ToastNotifier  toastNotifier = manager.create(NotifyType.TOAST);
//                        toastNotifier.create(NotifierToastType.SUCCESS,"Great Notification");
                        Notifier notify = manager.create(NotifyType.NOTIFIER);
                        notify.setHeader("Notification")
                                .setBody(text + "\nI am the new order")
                                .setPosition(NotificationPos.CENTER)
                                .setAnimation(true)
//                                .autoClose(false)
                                .setDuration(5.5)
                                .setButton("exit", ()->{

                                },"-fx-background-color: red;-fx-text-fill: white;")
                                .setButton("cancel",()->{})
                                .create();
//                        PromptNotifier prompt2 = manager.create(NotifyType.PROMPT);
//                        prompt2.setHeader("Prompt")
//                                .setLabel("Hi there this is a prompt \n"+text)
//                                .autoClose(false)
//                                .setAnimation(true)
//                                .setPlaceholder("Hello placeholder")
//                                .setButton("cancel",()->{})
//                                .setButton("save",(data)->{
//                                    Notifier notify = manager.create(NotifyType.NOTIFIER);
//                                    notify.setHeader("Notification")
//                                            .setBody(text + " " + data + "\nI am the new order")
//                                            .setPosition(NotificationPos.CENTER)
//                                            .setAnimation(true)
//                                            .autoClose(false)
//                                            .setButton("exit", ()->{
//
//                                            },"-fx-background-color: red;-fx-text-fill: white;")
//                                            .setButton("cancel",()->{})
//                                            .create();
//                                })
//                                .create();
                    })
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