package com.edubiz.notificationsjava.Notifications;

import com.edubiz.notificationsjava.util.Util;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

public class Toast extends BaseNotifications {
    public Toast() {}

    public void toast(String message,int delayInMilliseconds) {
        createToast(message,delayInMilliseconds);
    }

    private void createToast(String message,int delayInMilliseconds) {
//        create parent
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

//        create child pane
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setStyle("-fx-border: 0 0 2px 0;fx-border-color: transparent transparent green transparent;-fx-padding: 5px; -fx-background-color: white;");

        ImageView icon = new ImageView();
        icon.setFitHeight(10);
        icon.setFitWidth(10);

//        Label for messages
        Label text = new Label(message);
        text.setStyle("-fx-text-fill: black;-fx-font-size: 14px;");
        text.setWrapText(true);
        text.setTextAlignment(TextAlignment.LEFT);

//        set the components in the wrapper
        hBox.getChildren().addAll(icon,text);

//        set auto-sizing
        hBox.setMaxWidth(200);

        anchorPane.getChildren().add(hBox);
        root.getChildren().add(anchorPane);

        show();

        Util.timeOut(()-> Platform.runLater(this::close),delayInMilliseconds == 0? 3500:delayInMilliseconds);
    }
}
