package com.edubiz.notificationsjava.Notifications;

import com.edubiz.notificationsjava.util.Util;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

public class Toast extends BaseNotifications {
    public Toast() {}

    public void toast(String message,int delayInMilliseconds) {
        createToast(message,delayInMilliseconds);
    }

    private void createToast(String message,int delayInMilliseconds) {
        // create parent
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        // create child pane
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().add("toast");

        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_CIRCLE_FILL);
        icon.getStyleClass().add("icon");
        Label iconWrapper = new Label();
        iconWrapper.setGraphic(icon);
        iconWrapper.setAlignment(Pos.CENTER);
        iconWrapper.getStyleClass().add("toast-icon");

        // Label for messages
        Label text = new Label(message);
        text.getStyleClass().add("toast-message");
        text.setWrapText(true);
        text.setTextAlignment(TextAlignment.LEFT);

//        set the components in the wrapper
        hBox.getChildren().addAll(iconWrapper,text);

//        set auto-sizing
        hBox.setMaxWidth(200);

        anchorPane.getChildren().add(hBox);
        root.getChildren().add(anchorPane);

        show();

//        Util.timeOut(()-> Platform.runLater(this::close),delayInMilliseconds == 0? 3500:delayInMilliseconds);
    }
}
