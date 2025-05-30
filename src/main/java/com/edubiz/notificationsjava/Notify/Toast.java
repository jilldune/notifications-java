package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
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

import static com.edubiz.notificationsjava.NotifierUtil.NotifyUtils.timeOut;

public class Toast extends NotifyBase {
    private final double DURATION = .7;
    private final NotifyPos DEFAULT_POSITION = NotifyPos.TOP;

    public Toast(Stage stage) {
        super(stage, new HBox());
    }

    public void create(NotifyAlert type, String message) { createToast(type,message,DEFAULT_POSITION,true,DURATION); }

    public void create(NotifyAlert type, String message, NotifyPos position) { createToast(type,message,position,true,DURATION); }

    public void create(NotifyAlert type, String message, Boolean animation) { createToast(type,message,DEFAULT_POSITION,animation,DURATION); }

    public void create(NotifyAlert type, String message, double durationInSeconds) { createToast(type,message,DEFAULT_POSITION,true,durationInSeconds); }

    public void create(NotifyAlert type, String message, NotifyPos position, boolean animation) {
        createToast(type,message,position,animation,DURATION);
    }

    public void create(NotifyAlert type, String message, NotifyPos position,double durationInSeconds) {
        createToast(type,message,position,true,durationInSeconds);
    }

    public void create(NotifyAlert type, String message, boolean animation, double durationInSeconds) {
        createToast(type,message,DEFAULT_POSITION,animation,durationInSeconds);
    }

    public void create(NotifyAlert type, String message, NotifyPos position, boolean animation, double durationInSeconds) {
        createToast(type,message,position,animation,durationInSeconds);
    }

    private void createToast(NotifyAlert type, String message, NotifyPos position, boolean animation, double delayInSeconds) {
        // get/configure the toast type
        Ikon iconCode = getIkon(type); // icon
        String colorStyle = type.getTypeName(); // color style

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

        show(hBox,position,animation,delayInSeconds);

        autoCloseToast(delayInSeconds);
    }
    private static Ikon getIkon(NotifyAlert type) {
        return switch (type) {
            case SUCCESS -> RemixiconAL.CHECKBOX_CIRCLE_FILL;
            case ERROR -> RemixiconAL.CLOSE_CIRCLE_FILL;
            case ALERT -> RemixiconAL.ALERT_FILL;
            case NEUTRAL -> RemixiconAL.INDETERMINATE_CIRCLE_FILL;
            case INFO -> RemixiconAL.INFORMATION_FILL;
        };
    }

    // Auto closing the function
    private void autoCloseToast(double duration) {
        timeOut(this::run,duration);
    }

    private void run() {
        close();
    }
}

