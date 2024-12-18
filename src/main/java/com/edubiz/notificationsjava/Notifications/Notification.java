package com.edubiz.notificationsjava.Notifications;

import com.edubiz.notificationsjava.util.Util;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Map;

public class Notification extends BaseNotifications {

    public void notification(Map<String, Object> options){
        String header = (String) options.getOrDefault("header","Notification");
        String messageBody = (String) options.getOrDefault("messageBody","Notification body");
        Map<String,Map<String,Object>> buttons = (Map<String,Map<String,Object>>) options.getOrDefault("buttons",Map.of());
        Integer duration = (Integer) options.getOrDefault("duration",3500);
        Boolean autoClose = (Boolean) options.getOrDefault("autoClose",true);

        // create parent
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        // create child pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().add("notification");

        // create the head
        createHead(vBox,header);

        // create the body
        createBody(vBox,messageBody);

        // create footer
        createFooter(vBox,buttons);

        // set auto-sizing
        // vBox.setMaxWidth(200);

        anchorPane.getChildren().add(vBox);
        root.getChildren().add(anchorPane);

        show();


        // create the close function
        autoCloseNotification(autoClose,duration);
    }

    private void createHead(@NotNull VBox parent, String headerText) {
        // create the header text
        Label header = new Label(headerText);
        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().add("notification-title");

        // create the Icon
        HBox head = getHead(parent, header);
        head.getStyleClass().add("notification-head");

        // set the header
        parent.getChildren().add(head);
    }

    @NotNull
    private HBox getHead(@NotNull VBox parent, Label header) {
        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);
        icon.getStyleClass().add("icon");

        Label btnIcon = new Label();
        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);
        btnIcon.getStyleClass().add("notification-close");

        btnIcon.setOnMouseClicked(event -> close());

        // create the head container
        HBox head = new HBox(header,btnIcon);// Add label and close icon to the head
        head.setAlignment(Pos.CENTER_LEFT);
        head.maxWidth(parent.getWidth());

        return head;
    }

    // create Notification message body
    private void createBody(VBox parent, String bodyText) {
        // add to the parent
        parent.getChildren().add(getTextArea(parent, bodyText));
    }

    @NotNull
    private TextArea getTextArea(VBox parent, String bodyText) {
        TextArea messageArea = new TextArea();
        messageArea.setText(bodyText);
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        messageArea.setFocusTraversable(false);
        messageArea.setCursor(Cursor.DEFAULT);
        messageArea.getStyleClass().add("notification-body");

        return messageArea;
    }

    // create the Notification Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        HBox buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("notification-footer");
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
            Runnable action = (Runnable) properties.getOrDefault("action", (Runnable) () -> System.out.println(label + " clicked"));

            button.setOnAction(e -> {
                action.run();
                close();
            });

            buttonContainer.getChildren().add(button);
        });

        parent.getChildren().add(buttonContainer);
    }

    // Auto closing the function
    private void autoCloseNotification(Boolean autoClose,int duration) {
        if (!autoClose) return;

        Util.timeOut(this::run,duration == 0? 3500:duration);
    }

    private void run() {
        close();
    }
}
