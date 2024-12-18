package com.edubiz.notificationsjava;

import com.edubiz.notificationsjava.Notifications.Notification;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Entry extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        Toast
//        Toast toast = new Toast();
//        toast.toast("This is a toast, thing in the name of God ooo heyy heyy ehyy",0);

//        Notification
        Notification notif = new Notification();
        Map<String, Object> options = Map.of(
                "header","My new notification",
                "body","This is a test notification message. Thank you",
                "duration",0,
                "autoClose",false,
                "buttons",new LinkedHashMap<String,Map<String,Object>>(){{
                    put("cancel",Map.of(
                            "action",(Runnable)()-> System.out.println("action for button")
//                            "style","-fx-background-color: blue;-fx-text-fill: white;"
                    ));
                    put("delete",Map.of(
                            "action",(Runnable)()-> System.out.println("action for delete"),
                            "style","-fx-background-color: red;-fx-text-fill: white;"
                    ));
                }}
        );
        notif.notification(options);

//        Prompt prompt = new Prompt("Enter our name","name");
//        prompt.show();
    }

    public static void main(String[] args) {
        launch();
    }
}