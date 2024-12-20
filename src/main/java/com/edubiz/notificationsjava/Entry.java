package com.edubiz.notificationsjava;

import com.edubiz.notificationsjava.Notifications.Notification;
import com.edubiz.notificationsjava.Notifications.Prompt;
import com.edubiz.notificationsjava.Notifications.Toast;
import com.edubiz.notificationsjava.Notifications.ToastType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Entry extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // create  the root
        AnchorPane root = new AnchorPane();
        root.setPrefSize(800,600);
        root.setStyle("-fx-background-color: red;");

        Button button = new Button("See Notification");
        button.setOnAction(e -> {
            // Toast
            // Toast toast = new Toast(stage);
            // toast.toast(ToastType.ERROR,"This is a toast",5000);

            // Notification
            Notification notify = new Notification(stage);
            Map<String, Object> options = new HashMap<>();
            options.put("header", "My new notification");
            options.put("body", "This is a test notification message. Thank you");
            options.put("duration", 0);
            options.put("autoClose", false);
            options.put("buttons", new LinkedHashMap<String, Map<String, Object>>() {{
                put("cancel", Map.of(
                        "action", (Runnable) () -> System.out.println("action for button")
                        // "style","-fx-background-color: blue;-fx-text-fill: white;"
                ));
                put("delete", Map.of(
                        "action", (Runnable) () -> System.out.println("action for delete"),
                        "style", "-fx-background-color: red;-fx-text-fill: white;"
                ));
            }});
            notify.notification(options);

//            Prompt prompt = new Prompt(stage);
//            Map<String, Object> options = new HashMap<>();
//            options.put("header", "My Prompt");
//            options.put("type", "text");
//            options.put("placeholder", "placeholder");
//            options.put("label", "Enter your name");
//            options.put("position", "top-left");
//            options.put("duration", 0);
//            options.put("autoClose", false);
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
        stage.setTitle("Notification Demo");


        // Shoe the stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}