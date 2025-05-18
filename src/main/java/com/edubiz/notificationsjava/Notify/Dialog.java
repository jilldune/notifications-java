package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifyInput;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Dialog extends NotifyBase {
    private TextArea textArea;
    private TextField textField;
    private PasswordField passwordField;

    //    class variables
    private static String FIELD = "text";
    private String headerText = "Dialog";
    private NotifyInput type = NotifyInput.TEXT_FIELD;
    private String value = "";
    private String placeHolder = "";
    private String label = null;
    private NotifyPos position = NotifyPos.CENTER;
    private double durationInSeconds = 4.5;
    private boolean autoClose = false;
    private boolean animation = true;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();
    private boolean bindInputAction = false;
    private Integer buttonIndex = null;

    public Dialog(Stage stage) { super(stage, new VBox()); }

    private void createHead(@NotNull VBox parent, String headerText) {
        // create the header text
        Label header = new Label(headerText);
        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().add("dialog-title");

        // create the Icon
        HBox head = head(header);
        head.getStyleClass().add("dialog-head");

        // set the header
        parent.getChildren().add(head);
    }
    @NotNull
    private HBox head(Label header) {
        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);
        icon.getStyleClass().add("icon");

        Label btnIcon = new Label();
        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);
        btnIcon.getStyleClass().addAll("dialog-close","close-btn");

        btnIcon.setOnMouseClicked(event -> close());

        // create the head container
        HBox head = new HBox(header,btnIcon);// Add label and close icon to the head
        head.setAlignment(Pos.CENTER_LEFT);

        return head;
    }

    // create MyNotifier message body
    private void createBody(VBox parent) {
        // create a body container
        VBox bodyPane = new VBox();
        bodyPane.getStyleClass().add("dialog-body");

        // create label
        if (label != null) {
            Label inputLabel = new Label();
            inputLabel.setText(this.label);
            inputLabel.setTextAlignment(TextAlignment.LEFT);
            inputLabel.getStyleClass().add("dialog-label");

            // Create a divider
            VBox divider = new VBox();
            divider.getStyleClass().add("dialog-divider");


            bodyPane.getChildren().addAll(inputLabel,divider);
        }

        switch(this.type) {
            case TEXT_FIELD -> {
                textField = new TextField();
                textField.getStyleClass().addAll("dialog-text",type.getValue());
                textField.setPromptText(this.placeHolder);
                textField.setText(this.value);

                FIELD = "TEXT_FIELD";

                bodyPane.getChildren().add(textField);
            }
            case PASSWORD_FIELD -> {
                passwordField = new PasswordField();
                passwordField.getStyleClass().addAll("dialog-text",type.getValue());
                passwordField.setPromptText(this.placeHolder);
                passwordField.setText(this.value);

                FIELD = "PASSWORD";

                bodyPane.getChildren().add(passwordField);
            }
            case TEXTAREA -> {
                textArea = new TextArea();
                textArea.getStyleClass().addAll("dialog-text",type.getValue());
                textArea.setPromptText(this.placeHolder);
                textArea.setWrapText(true);
                textArea.setText(this.value);

                FIELD = "TEXT_AREA";

                bodyPane.getChildren().add(textArea);
            }
        }

        // add to the parent
        parent.getChildren().add(bodyPane);
    }
    // Bind input action
    private void bindInputAction(int index,EventHandler<ActionEvent> handler) {
        if (! bindInputAction) return;
        if (buttonIndex == null) return;

        if (buttonIndex != index) return;

        Node input = getInput();

        if (input instanceof TextField) ((TextField) input).setOnAction(handler);

        if (input instanceof PasswordField) ((PasswordField) input).setOnAction(handler);

        if (input instanceof TextArea) {
            input.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                    handler.handle(new ActionEvent());
                }
            });
        }
    }

    // create the MyNotifier Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        // main footer container
        VBox footerContainer = new VBox();
        footerContainer.getStyleClass().add("dialog-footer");

        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getStyleClass().add("button-container");

        // create buttons
        AtomicInteger i = new AtomicInteger(0);
        buttons.forEach((label, properties) -> {
            if (properties != null) {
                Button button = new Button(label);

                // apply style if provided
                Object styleObj = properties.get("style");
                if (styleObj instanceof String styles) { button.setStyle(styles); }

                // Add default button style
                button.getStyleClass().add("button");

                // bind action to button
                Object actionObj = properties.get("action");
                EventHandler<ActionEvent> handler = getActionEventEventHandler(actionObj);

                // bind action events
                button.setOnAction(handler);
                bindInputAction(i.get(),handler);

                buttonContainer.getChildren().add(button);
            }
            else {
                System.err.println("Properties map is null for button: " + label);
            }

            i.getAndIncrement();
        });

        // ADD BUTTONS TO THE FOOTER
        footerContainer.getChildren().add(buttonContainer);

        parent.getChildren().add(footerContainer);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private EventHandler<ActionEvent> getActionEventEventHandler(Object actionObj) {
        EventHandler<ActionEvent> handler;
        if (actionObj instanceof Runnable action) {
            handler = ev -> {
                action.run();
                close();
            };
        }
        else if (actionObj instanceof Consumer) {
            Consumer<String> action = (Consumer<String>) actionObj;
            handler = ev -> {
                action.accept(getUserInput());
                close();
            };
        }
        else {
            Runnable defaultAction = () -> {};
            handler = e -> {
                defaultAction.run();
                close();
            };
        }

        return handler;
    }

    // Auto closing the function
    private void autoClosePrompt(boolean autoClose, double duration) {
        if (!autoClose) return;

        NotifyUtils.timeOut(this::run,duration == 0? this.durationInSeconds:duration);
    }
    private void run() {
        close();
    }
    private String getUserInput() {
        switch (FIELD.toLowerCase()) {
            case "text_field" -> {
                return textField.getText().trim();
            }
            case "password" -> {
                return passwordField.getText().trim();
            }
            case "text_area" -> {
                return textArea.getText().trim();
            }
        }

        return "";
    }
    private Node getInput() {
        switch (FIELD.toLowerCase()) {
            case "text_field" -> {
              return textField;
            }
            case "password" -> {
                return passwordField;
            }
            case "text_area" -> {
                return textArea;
            }
        }

        return null;
    }
    private VBox parent() {
        VBox vBox = (VBox) getLayout();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("dialog","parent-container");

        return vBox;
    }
    
    // ========== Exposed creator Methods ==========

    public Dialog setHeader(String header) {
        headerText = header;
        return this;
    }
    public Dialog setType(NotifyInput type) {
        this.type = type;
        return this;
    }
    public Dialog setPlaceholder(String placeHolder) {
        this.placeHolder = placeHolder;
        return this;
    }
    public Dialog setValue(String value) {
        this.value = value;
        return this;
    }
    public Dialog setLabel(String label) {
        this.label = label;
        return this;
    }
    public Dialog setPosition(NotifyPos position) {
        this.position = position;
        return this;
    }
    public Dialog setDuration(Double durationInSeconds) {
        this.durationInSeconds = durationInSeconds < .7? this.durationInSeconds:durationInSeconds;
        return this;
    }
    public Dialog autoClose(Boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }
    public Dialog setAnimation(Boolean animation) {
        this.animation = animation;
        return this;
    }
    public Dialog setButton(String label, Consumer<String> action, String style) {
        this.buttons.put(label,Map.of("action",action,"style",style));
        return this;
    }
    public Dialog setButton(String label, Consumer<String> action) {
        this.buttons.put(label,Map.of("action",action));
        return this;
    }
    public Dialog setButton(String label, Runnable action) {
        this.buttons.put(label,Map.of("action",action));
        return this;
    }
    public Dialog setButton(String label, Runnable action, String style) {
        this.buttons.put(label,Map.of("action",action,"style",style));
        return this;
    }
    public Dialog bindInputAction(int buttonIndex) {
        bindInputAction = true;
        this.buttonIndex = buttonIndex;

        return this;
    }
    public void create() {
        // create dialog pane
        VBox vBox = parent();

        // create the head
        createHead(vBox, headerText);

        // create the body
        createBody(vBox);

        // create footer
        createFooter(vBox, buttons);

        // showing the dialog
        show(vBox,position,animation,durationInSeconds);

        // create the close function
        autoClosePrompt(autoClose, durationInSeconds);
    }

}
