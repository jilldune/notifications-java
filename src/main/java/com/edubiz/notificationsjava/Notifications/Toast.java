package com.edubiz.notificationsjava.Notifications;

import com.edubiz.notificationsjava.util.Util;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

public class Toast extends BaseNotifications {
    private final int DURATION = 3500;

    public Toast() {}

    public void toast(ToastType type,String message,int delayInMilliseconds) {
        createToast(type,message,delayInMilliseconds);
    }

    public void toast(ToastType type,String message) {
        createToast(type,message,DURATION);
    }

    private void createToast(ToastType type,String message,int delayInMilliseconds) {
        // create parent
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        // get/configure the toast type
        String colorStyle;
        Ikon iconCode;
        switch (type) {
            case SUCCESS -> {
                colorStyle = type.getTypeName();
                iconCode = RemixiconAL.CHECKBOX_CIRCLE_FILL;
            }
            case ERROR -> {
                colorStyle = type.getTypeName();
                iconCode = RemixiconAL.CLOSE_CIRCLE_FILL;
            }
            case ALERT -> {
                colorStyle = type.getTypeName();
                iconCode = RemixiconAL.ALERT_FILL;
            }
            case NEUTRAL -> {
                colorStyle = type.getTypeName();
                iconCode = RemixiconAL.INDETERMINATE_CIRCLE_FILL;
            }
            case INFO -> {
                colorStyle = type.getTypeName();
                iconCode = RemixiconAL.INFORMATION_FILL;
            }
            default -> throw new IllegalArgumentException("Unknown Toast Type");
        }

        // create child pane
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().addAll("toast",colorStyle);

        FontIcon icon = new FontIcon(iconCode);
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

//        Util.timeOut(()-> Platform.runLater(this::close),delayInMilliseconds == 0? DURATION:delayInMilliseconds);
    }
}

