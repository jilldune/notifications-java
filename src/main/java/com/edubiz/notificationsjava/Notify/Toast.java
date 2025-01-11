package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifyAlert;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

public class Toast extends NotifyBase {
    private final double DURATION = 4.5;
    private final NotifyPos DEFAULT_POSITION = NotifyPos.TOP_RIGHT;

    public Toast(Stage stage) {
        super(stage, new HBox());
    }

    public void create(NotifyAlert type, String message) { createToast(type,message,DEFAULT_POSITION,true,DURATION); }

    public void create(NotifyAlert type, String message, NotifyPos position) { createToast(type,message,position,true,DURATION); }

    public void create(NotifyAlert type, String message, double delayInSeconds) { createToast(type,message,DEFAULT_POSITION,true,delayInSeconds); }

    public void create(NotifyAlert type, String message, NotifyPos position, boolean animation, double delayInSeconds) {
        createToast(type,message,position,animation,delayInSeconds);
    }

    private void createToast(NotifyAlert type, String message, NotifyPos position, boolean animation, double delayInSeconds) {
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
        HBox hBox = (HBox) getLayout();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getStyleClass().addAll("toast","parent-container",colorStyle);

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

        // set the components in the wrapper
        hBox.getChildren().addAll(iconWrapper,text);

        // set auto-sizing
        hBox.setMaxWidth(200);

        delayInSeconds = delayInSeconds < .7 ? DURATION:delayInSeconds;

        show(hBox,position,animation,delayInSeconds);

        autoCloseToast(delayInSeconds);
    }

    // Auto closing the function
    private void autoCloseToast(double duration) {
        NotifyUtils.timeOut(this::run,duration);
    }

    private void run() {
        close();
    }
}

