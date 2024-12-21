package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.NotifierUtil.Helper;
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
import org.kordamp.ikonli.remixicon.RemixiconAL;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Notifier extends BaseNotifier {
    //    class variables
    private String headerText = "Notifier";
    private String body = "";
    private double durationInSeconds = 3.5;
    private NotificationPos position = NotificationPos.CENTER;
    private Boolean autoClose = true;
    private Boolean animation = true;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();


    public Notifier(Stage stage) {
        super(stage);
    }

    public void notification(Map<String, Object> options){
        String header = (String) options.getOrDefault("header",this.headerText);
        String messageBody = (String) options.getOrDefault("messageBody",this.body);
        Map<String,Map<String,Object>> buttons = (Map<String,Map<String,Object>>) options.getOrDefault("buttons",Map.of());
        Double duration = (Double) options.getOrDefault("duration", this.durationInSeconds);
        Boolean animation = (Boolean) options.getOrDefault("animation", this.animation);
        Boolean autoClose = (Boolean) options.getOrDefault("autoClose",this.autoClose);
        NotificationPos position = (NotificationPos) options.getOrDefault("position", this.position);

        // create child pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("notification","parent-container");

        // create the head
        createHead(vBox,header);

        // create the body
        createBody(vBox,messageBody);

        // create footer
        createFooter(vBox,buttons);

        show(vBox,position,animation,duration);

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
    private void autoCloseNotification(Boolean autoClose,double duration) {
        if (!autoClose) return;

        Helper.timeOut(this::run,duration == 0? 3.5:duration);
    }

    // Plain Methods
    public void setHeader(String headerText) {
        this.headerText = headerText;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public void setPosition(NotificationPos position) {
        this.position = position;
    }
    public void setDuration(Double durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    public void autoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }
    public void setAnimation(Boolean animation) {
        this.animation = animation;
    }
    public void setButton(String label, Runnable action, String style) {
        this.buttons.put(label,Map.of("action",action,"style",style));
    }
    public void setButton(String label, Runnable action) {
        this.buttons.put(label,Map.of("action",action));
    }

    public void create() {
        // create prompt pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("notification","parent-container");

        // create the head
        createHead(vBox, this.headerText);

        // create the body
        createBody(vBox, this.body);

        // create footer
        createFooter(vBox, this.buttons);

        show(vBox, this.position, this.animation, this.durationInSeconds);

        // create the close function
        autoCloseNotification(this.autoClose, this.durationInSeconds);
    }

    private void run() {
        close();
    }
}
