package com.edubiz.notificationsjava.Notifications;

import com.edubiz.notificationsjava.util.Util;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.Map;
import java.util.function.Consumer;

public class Prompt extends BaseNotifications {
    private TextArea textArea;
    private TextField textField;
    private static boolean isTextField = true;

    public Prompt(Stage stage) {
        super(stage);
    }

    public void prompt(Map<String, Object> options) {
        String header = (String) options.getOrDefault("header", "Prompt");
        Double duration = (Double) options.getOrDefault("duration", 3.5);
        Boolean animation = (Boolean) options.getOrDefault("animation", true);
        Boolean autoClose = (Boolean) options.getOrDefault("autoClose", true);
        NotificationPos position = (NotificationPos) options.getOrDefault("position", "center");

        // create child pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("prompt","parent-container");

        // create the head
        createHead(vBox, header);

        // create the body
        createBody(vBox, options);

        // create footer
        if (options.containsKey("buttons")) {
            Map<String, Map<String, Object>> buttons = (Map<String, Map<String, Object>>) options.getOrDefault("buttons", Map.of());
            createFooter(vBox, buttons);
        }

        show(vBox,position,animation,duration);

        // create the close function
        autoClosePrompt(autoClose, duration);
    }

    private void createHead(@NotNull VBox parent, String headerText) {
        // create the header text
        Label header = new Label(headerText);
        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().add("prompt-title");

        // create the Icon
        HBox head = getHead(header);
        head.getStyleClass().add("prompt-head");

        // set the header
        parent.getChildren().add(head);
    }

    @NotNull
    private HBox getHead(Label header) {
        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);
        icon.getStyleClass().add("icon");

        Label btnIcon = new Label();
        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);
        btnIcon.getStyleClass().add("prompt-close");

        btnIcon.setOnMouseClicked(event -> close());

        // create the head container
        HBox head = new HBox(header,btnIcon);// Add label and close icon to the head
        head.setAlignment(Pos.CENTER_LEFT);

        return head;
    }

    // create Notification message body
    private void createBody(VBox parent, Map<String, Object> options) {
        String inputType = (String) options.getOrDefault("type", "text");
        String placeHolder = (String) options.getOrDefault("placeHolder", "Enter text");
        String value = (String) options.getOrDefault("value", "");
        String label = (String) options.getOrDefault("label", null);

        // create a body container
        VBox bodyPane = new VBox();
        bodyPane.getStyleClass().add("prompt-body");

        // create label
        if (label != null) {
            Label inputLabel = new Label();
            inputLabel.setText(label);
            inputLabel.setTextAlignment(TextAlignment.LEFT);
            inputLabel.getStyleClass().add("prompt-label");

            bodyPane.getChildren().add(inputLabel);
        }

        // Create the input
        if (inputType != null) {
            switch(inputType.toLowerCase()) {
                case "text" -> {
                    textField = new TextField();
                    textField.getStyleClass().addAll("prompt-text","text");
                    textField.setPromptText(placeHolder);
                    textField.setText(value);

                    isTextField = true;

                    bodyPane.getChildren().add(textField);
                }
                case "textarea" -> {
                    textArea = new TextArea();
                    textArea.getStyleClass().addAll("prompt-text","textarea");
                    textArea.setPromptText(placeHolder);
                    textArea.setText(value);

                    isTextField = false;

                    bodyPane.getChildren().add(textArea);
                }
            }
        }

        // add to the parent
        parent.getChildren().add(bodyPane);
    }

    // create the Notification Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        HBox buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("prompt-footer");
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttons.forEach((label,properties) -> {
            Button button = new Button(label);

            // apply style if provided
            String styles = (String) properties.getOrDefault("style","");
            button.setStyle(styles);
            button.getStyleClass().add("button");

            // apply icon if provided
            String iconUrl = (String) properties.get("icon");
            if (iconUrl != null) {
                ImageView icon = new ImageView(new Image(iconUrl));
                icon.setFitWidth(16);
                icon.setFitHeight(16);
                button.setGraphic(icon);
            }

            // bind action to button
            Consumer<String> action = (Consumer<String>) properties.getOrDefault("action", (Consumer<String>) (String text) -> System.out.println(label + " clicked"));

            button.setOnAction(e -> {
                action.accept(getUserInput());
                close();
            });

            buttonContainer.getChildren().add(button);
        });

        parent.getChildren().add(buttonContainer);
    }

    // Auto closing the function
    private void autoClosePrompt(Boolean autoClose, double duration) {
        if (!autoClose) return;

        Util.timeOut(this::run,duration == 0? 3500:duration);
    }

    private void run() {
        close();
    }

    public String getUserInput() {
        if (isTextField) return textField.getText().trim();

        return textArea.getText().trim();
    }
}
