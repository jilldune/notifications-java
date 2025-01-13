package com.edubiz.notificationsjava.test;

import com.edubiz.notificationsjava.Managers.NotifyManager;
import com.edubiz.notificationsjava.NotifierUtil.NotifyAlert;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifyInput;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.Notify.*;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EntryNotification extends Application {
    @Override
    public void start(Stage stage) {
        // create  the root
        AnchorPane root = new AnchorPane();
        root.setPrefSize(800,600);
        root.setStyle("-fx-background-color: red;");

        NotifyManager manager = new NotifyManager(stage);

        stage.setOnCloseRequest(ev -> manager.shutDown());

        Button button = new Button("See MyNotifier");
        button.setCursor(Cursor.HAND);
        button.setOnAction(e -> {
            // Prompt
            Prompt prompt = manager.create(NotifyType.PROMPT);
            prompt.setHeader("Prompt")
                    .setBody("Hi there this is a prompt \nclick my buttons to view other components")
                    .autoClose(false)
                    .setAnimation(true)
                    .setButton("dialog",()->{
                        Dialog dialog = manager.create(NotifyType.DIALOG);
                        dialog.setOnCloseRequest(()-> System.out.println("On close request..."));
                        dialog.setOnClosed(()-> System.out.println("on close..."));
                        dialog.setHeader("Dialog Notification")
                                .setLabel("This is a dialogue body\nTell us about you.")
                                .setType(NotifyInput.TEXTAREA)
                                .create();

                    })
                    .setButton("notification",()->{
                        Notification notify = manager.create(NotifyType.NOTIFICATION);
                        notify.setHeader("Notification")
                                .setBody("I am a notification")
                                .setPosition(NotifyPos.CENTER)
                                .setAnimation(true)
                                .autoClose(false)
                                .setDuration(5.5)
                                .setButton("exit", ()->{

                                },"-fx-background-color: red;-fx-text-fill: white;")
                                .setButton("cancel",()->{})
                                .create();
                    })
                    .setButton("toast",()->{
                        Toast  toastNotifier = manager.create(NotifyType.TOAST);
                        toastNotifier.create(NotifyAlert.SUCCESS,"I am a toast");
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