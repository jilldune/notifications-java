package com.edubiz.notificationsjava.Notifications;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Prompt extends BaseNotifications {
    private String userInput;

    public Prompt(String title,String placeholder) {
        VBox vbox = new VBox(10);
        TextField textField = new TextField(title);
        textField.setPromptText(placeholder);
        Button submitButton = new Button("submit");

        submitButton.setOnAction(e -> {
            userInput = textField.getText();
            System.out.println(getUserInput());
            close();
        });

        vbox.getChildren().addAll(textField,submitButton);
        root.getChildren().add(vbox);
    }

    public String getUserInput() {
        return userInput;
    }
}
