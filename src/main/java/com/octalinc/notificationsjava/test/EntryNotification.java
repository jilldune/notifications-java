package com.octalinc.notificationsjava.test;

import com.octalinc.notificationsjava.Managers.NotifyManager;
import com.octalinc.notificationsjava.NotifierUtil.NotifyAlert;
import com.octalinc.notificationsjava.NotifierUtil.NotifyPos;
import com.octalinc.notificationsjava.NotifierUtil.NotifyInput;
import com.octalinc.notificationsjava.NotifierUtil.NotifyType;
import com.octalinc.notificationsjava.Notify.Dialog;
import com.octalinc.notificationsjava.Notify.Notification;
import com.octalinc.notificationsjava.Notify.Prompt;
import com.octalinc.notificationsjava.Notify.Toast;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EntryNotification extends Application {
    @Override
    public void start(Stage stage) {
        // create the root
        StackPane root = new StackPane();
        root.setPrefSize(800,600);
        root.setStyle("-fx-background-color: red;");

        NotifyManager manager = new NotifyManager(stage);

        stage.setOnCloseRequest(ev -> manager.shutDown());

        Button notification = getNotification(manager);
        // DIALOG
        Button dialog = getDialog(manager);
        // TOAST
        Button toast = new Button("Toast");
        toast.setCursor(Cursor.HAND);
        toast.setOnAction(e -> {
            Toast toastNotifier = manager.create(NotifyType.TOAST);
            toastNotifier.create(NotifyAlert.SUCCESS,"I am a toast");
        });
        // PROMPT
        Button prompt = getPrompt(manager);

        VBox container = new VBox(10);
        container.getChildren().addAll(notification,prompt,toast,dialog);
        container.setAlignment(Pos.CENTER);
        container.setFillWidth(true);

        root.getChildren().add(container);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("MyNotifier Demo");


        // Show the stage
        stage.show();
    }
    private static Button getPrompt(NotifyManager manager) {
        Button prompt = new Button("Prompt");
        prompt.setCursor(Cursor.HAND);
        prompt.setOnAction(e -> {
            // Prompt
            Prompt promptNotify = manager.create(NotifyType.PROMPT);
            promptNotify.setHeader("Prompt")
                    .setBody("Hi there this is a prompt \nclick my buttons to view other components")
                    .autoClose(false)
                    .setAnimation(true)
                    .setButton("cancel",()->{})
                    .create();
        });
        return prompt;
    }

    private static Button getDialog(NotifyManager manager) {
        Button dialog = new Button("Dialog");
        dialog.setCursor(Cursor.HAND);
        dialog.setOnAction(e -> {
            Dialog dialogNotification = manager.create(NotifyType.DIALOG);
            dialogNotification.setHeader("Dialog Notification")
                    .setButton("save",(text) -> System.out.println(text))
                    .setButton("cancel",() -> {})
                    .setButton("cancel5 me",() -> {})
                    .bindInputAction(0)
                    .setLabel("This is a dialogue body\nTell us about you.")
                    .setType(NotifyInput.TEXTAREA)
                    .create();
        });
        return dialog;
    }
    private static Button getNotification(NotifyManager manager) {
        Button notification = new Button("Notification");
        notification.setCursor(Cursor.HAND);
        notification.setOnAction(e -> {
            Notification notify = manager.create(NotifyType.NOTIFICATION);
            notify.setHeader("Notification")
                    .setBody("I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation I am a notification for the next generation")
                    .setPosition(NotifyPos.TOP)
                    .setAnimation(true)
                    .setAlertType(NotifyAlert.ALERT)
                    .setButton("exit", ()-> System.exit(0),"-fx-background-color: red;-fx-border-color: red;-fx-text-fill: white;")
                    .setButton("cancel",()->{})
                    .setButton("cancel me",()->{
                        Toast toast = manager.create(NotifyType.TOAST);
                        toast.create(NotifyAlert.ERROR,"something happened");
                    })
                    .create();
        });
        return notification;
    }

    public static void main(String[] args) {
        launch();
    }
}