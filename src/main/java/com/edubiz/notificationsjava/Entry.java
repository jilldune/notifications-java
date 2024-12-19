package com.edubiz.notificationsjava;

import com.edubiz.notificationsjava.Notifications.Notification;
import com.edubiz.notificationsjava.Notifications.Prompt;
import com.edubiz.notificationsjava.Notifications.Toast;
import com.edubiz.notificationsjava.Notifications.ToastType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Entry extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Toast
        // Toast toast = new Toast();
        // toast.toast(ToastType.ERROR,"This is a toast");

        // Notification
//        Notification notify = new Notification();
//        Map<String, Object> options = new HashMap<>();
//        options.put("header", "My new notification");
//        options.put("body", "This is a test notification message. Thank you");
//        options.put("duration", 0);
//        options.put("autoClose", false);
//        options.put("buttons", new LinkedHashMap<String, Map<String, Object>>() {{
//            put("cancel", Map.of(
//                    "action", (Runnable) () -> System.out.println("action for button")
//                    // "style","-fx-background-color: blue;-fx-text-fill: white;"
//            ));
//            put("delete", Map.of(
//                    "action", (Runnable) () -> System.out.println("action for delete"),
//                    "style", "-fx-background-color: red;-fx-text-fill: white;"
//            ));
//        }});
//        notify.notification(options);

        Prompt prompt = new Prompt();
        Map<String, Object> options = new HashMap<>();
        options.put("header", "My new notification");
        options.put("type", "textarea");
        options.put("placeholder", "placeholder");
        options.put("label", "Enter your name");
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
        prompt.prompt(options);
    }

    public static void main(String[] args) {
        launch();
    }
}