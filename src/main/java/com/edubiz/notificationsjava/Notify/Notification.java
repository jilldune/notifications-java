package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
import com.edubiz.notificationsjava.NotifierUtil.NotifyAlert;
import com.edubiz.notificationsjava.NotifierUtil.NotifyType;
import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.LinkedHashMap;
import java.util.Map;

public class Notification extends NotifyBase {
    //    class variables
    private String headerText = "Notifier";
    private String body = "";
    private double durationInSeconds = 3.5;
    private NotifyPos position = NotifyPos.CENTER;
    private Boolean autoClose = false;
    private Boolean animation = true;
    private NotifyAlert alertType = NotifyAlert.NEUTRAL;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();

    public Notification(Stage stage) { super(stage, new VBox()); }

    private void createHead(@NotNull VBox parent, String headerText) {
        // create the header text
        Label header = new Label(headerText);
        String colorStyle = "";
        Ikon iconCode = null;
        switch (alertType) {
            case SUCCESS -> {
                colorStyle = alertType.getTypeName();
                iconCode = RemixiconAL.CHECKBOX_CIRCLE_FILL;
            }
            case ERROR -> {
                colorStyle = alertType.getTypeName();
                iconCode = RemixiconAL.CLOSE_CIRCLE_FILL;
            }
            case ALERT -> {
                colorStyle = alertType.getTypeName();
                iconCode = RemixiconAL.ALERT_FILL;
            }
            case NEUTRAL -> {
                colorStyle = alertType.getTypeName();
                iconCode = RemixiconAL.INDETERMINATE_CIRCLE_FILL;
            }
            case INFO -> {
                colorStyle = alertType.getTypeName();
                iconCode = RemixiconAL.INFORMATION_FILL;
            }
            default -> {}
        }
        if (! colorStyle.equalsIgnoreCase("neutral") && iconCode != null) {
            FontIcon icon = new FontIcon(iconCode);
            icon.getStyleClass().add("icon");
            header.setGraphic(icon);
            header.setGraphicTextGap(2.0);
        }
        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().addAll("notification-title",colorStyle);

        // create the Icon
        HBox head = getHead(header);
        head.getStyleClass().add("notification-head");

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
        btnIcon.getStyleClass().addAll("notification-close","close-btn");

        btnIcon.setOnMouseClicked(event -> close());

        // create the head container
        HBox head = new HBox(header,btnIcon);// Add label and close icon to the head
        head.setAlignment(Pos.CENTER_LEFT);

        return head;
    }

    // create MyNotifier message body
    private void createBody(VBox parent, String bodyText) {
        // add to the parent
        parent.getChildren().add(getTextArea(bodyText));
    }
    @NotNull
    private TextArea getTextArea(String bodyText) {
        TextArea messageArea = new TextArea();
        messageArea.setText(bodyText);
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        messageArea.setFocusTraversable(false);
        messageArea.setCursor(Cursor.DEFAULT);
        messageArea.getStyleClass().add("notification-body");

        return messageArea;
    }

    // create the MyNotifier Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        // main footer container
        VBox footerContainer = new VBox();
        footerContainer.getStyleClass().add("notification-footer");

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getStyleClass().add("button-container");

        // create buttons
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

        // ADD BUTTONS TO THE FOOTER
        footerContainer.getChildren().add(buttonContainer);

        parent.getChildren().add(footerContainer);
    }
    // Auto closing the function
    private void autoCloseNotification(Boolean autoClose,double duration) {
        if (!autoClose) return;

        NotifyUtils.timeOut(this::run,duration == 0? this.durationInSeconds:duration);
    }
    private VBox parent() {
        VBox vBox = (VBox) getLayout();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("notification","parent-container");

        return vBox;
    }

    // =============== Plain Methods ==================

    public Notification setHeader(String headerText) {
        this.headerText = headerText;
        return this;
    }
    public Notification setBody(String body) {
        this.body = body;
        return this;
    }
    public Notification setPosition(NotifyPos position) {
        this.position = position;
        return this;
    }
    public Notification setDuration(Double durationInSeconds) {
        this.durationInSeconds = (durationInSeconds < .7) ? this.durationInSeconds : durationInSeconds;
        return this;
    }
    public Notification setAlertType(NotifyAlert type) {
        this.alertType = type;
        return this;
    }
    public Notification autoClose(Boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }
    public Notification setAnimation(Boolean animation) {
        this.animation = animation;
        return this;
    }
    public Notification setButton(String label, Runnable action, String style) {
        this.buttons.put(label,Map.of("action",action,"style",style));
        return this;
    }
    public Notification setButton(String label, Runnable action) {
        this.buttons.put(label,Map.of("action",action));
        return this;
    }

    public void create() {
        // create prompt pane
        VBox vBox = parent();

        // create the head
        createHead(vBox, this.headerText);

        // create the body
        createBody(vBox, this.body);

        // create footer
        createFooter(vBox, this.buttons);

        // showing the notification
        show(vBox, this.position, this.animation, this.durationInSeconds);

        // create the close function
        autoCloseNotification(this.autoClose, this.durationInSeconds);
    }

    private void run() { close(); }
}
