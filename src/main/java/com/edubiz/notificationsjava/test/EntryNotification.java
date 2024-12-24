package com.edubiz.notificationsjava.test;

import com.edubiz.notificationsjava.Managers.NotifyManager;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.Notify.*;
import javafx.application.Application;
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
        button.setOnAction(e -> {
//            ToastNotifier toast = manager.create(NotifyType.TOAST);
//            toast.create(NotifierToastType.SUCCESS,"This is a brave toast",10.0);

            PromptNotifier prompt = manager.create(NotifyType.PROMPT);
            prompt.setHeader("Prompt")
                    .setLabel("Hi there this is a prompt \n I am doing great")
                    .setPlaceholder("Hello placeholder").create();
//            PromptNotifier prompt = new PromptNotifier(stage);
//            ToastNotifier toast = new ToastNotifier(stage);
//            Notifier notify = new Notifier(stage);

            // Toast
//             ToastNotifier toast = new ToastNotifier(stage);
//             toast.toast(ToastType.ERROR,"This is a toast",NotificationPos.CENTER,true,5);

            // Notifier
//            Notifier notify = new Notifier(stage);
//            notify.setHeader("Notification");
//            notify.setBody("This is the body of the Notifier");
//            notify.setPosition(NotificationPos.CENTER);
//            notify.setDuration(4.5);
//            notify.autoClose(false);
//            notify.setAnimation(true);
//            notify.setButton("ok", () -> {
//                System.out.println("text");
//            },"-fx-background-color: blue;-fx-text-fill: white;");
//            notify.setButton("cancel", () -> {
//                System.out.println("text");
//            },"-fx-background-color: green;-fx-text-fill: white;");
//            notify.create();
            // Map Method
//            Map<String, Object> options = new HashMap<>();
//            options.put("header", "My new notification");
//            options.put("body", "This is a test notification message. Thank you");
//            options.put("duration", 5.0);
//            options.put("position", NotificationPos.CENTER);
//            options.put("autoClose", false);
//            options.put("buttons", new LinkedHashMap<String, Map<String, Object>>() {{
//                put("cancel", Map.of(
//                        "action", (Runnable) () -> System.out.println("action for button")
//                        // "style","-fx-background-color: blue;-fx-text-fill: white;"
//                ));
//                put("delete", Map.of(
//                        "action", (Runnable) () -> System.out.println("action for delete"),
//                        "style", "-fx-background-color: red;-fx-text-fill: white;"
//                ));
//            }});
//            notify.notification(options);

//          ======= PROMPT ========
//            PromptNotifier prompt = new PromptNotifier(stage);
//            Plain Methods
//            prompt.setHeader("Prompt Notification");
//            prompt.setType(NotifierInputType.TEXT);
//            prompt.setPlaceholder("your name");
//            prompt.setLabel("Enter name");
//            prompt.setPosition(NotificationPos.CENTER);
//            prompt.setDuration(5.0);
//            prompt.autoClose(false);
//            prompt.setAnimation(true);
//            prompt.setButton("ok", (text) -> {
//                System.out.println(text);
//            },"-fx-background-color: blue;-fx-text-fill: white;");
//            prompt.setButton("cancel", (text) -> {
//                System.out.println(text);
//            },"-fx-background-color: green;-fx-text-fill: white;");
//            prompt.create();

//            === Map Method ===
//            Map<String, Object> options = new HashMap<>();
//            options.put("header", "My NotifierPrompt");
//            options.put("type", NotifierInputType.TEXT);
//            options.put("placeholder", "placeholder");
//            options.put("label", "Enter your name");
//            options.put("position", NotificationPos.CENTER);
//            options.put("duration", 0.0);
//            options.put("autoClose", false);
//            options.put("animation", true);
//            options.put("buttons", new LinkedHashMap<String, Map<String, Object>>() {{
//                put("cancel", Map.of(
//                        "action", (Consumer<String>) (text) -> {
//                            System.out.println(text);
//                        }
//                        // "style","-fx-background-color: blue;-fx-text-fill: white;"
//                ));
//                put("delete", Map.of(
//                        "action", (Consumer<String>) (text) -> {
//                            System.out.println(text);
//                        },
//                        "style", "-fx-background-color: red;-fx-text-fill: white;"
//                ));
//            }});
//            prompt.prompt(options);
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