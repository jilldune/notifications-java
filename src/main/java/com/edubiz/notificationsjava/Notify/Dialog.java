package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifyInput;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Dialog extends NotifyBase {
    private TextArea textArea;
    private TextField textField;

    //    class variables
    private static boolean isTextField = true;
    private String headerText = "Dialog";
    private NotifyInput type = NotifyInput.TEXT;
    private String value = "";
    private String placeHolder = "";
    private String label = null;
    private NotifyPos position = NotifyPos.CENTER;
    private double durationInSeconds = 4.5;
    private Boolean autoClose = true;
    private Boolean animation = true;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();

    public Dialog(Stage stage) { super(stage, new VBox()); }

    private void createHead(@NotNull VBox parent, String headerText) {
        // create the header text
        Label header = new Label(headerText);
        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().add("prompt-title");

        // create the Icon
        HBox head = head(header);
        head.getStyleClass().add("prompt-head");

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
        btnIcon.getStyleClass().addAll("prompt-close","close-btn");

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
        bodyPane.getStyleClass().add("prompt-body");

        // create label
        if (label != null) {
            Label inputLabel = new Label();
            inputLabel.setText(this.label);
            inputLabel.setTextAlignment(TextAlignment.LEFT);
            inputLabel.getStyleClass().add("prompt-label");

            // Create a divider
            VBox divider = new VBox();
            divider.getStyleClass().add("prompt-divider");


            bodyPane.getChildren().addAll(inputLabel,divider);
        }

        switch(this.type) {
            case TEXT -> {
                textField = new TextField();
                textField.getStyleClass().addAll("prompt-text",type.getValue());
                textField.setPromptText(this.placeHolder);
                textField.setText(this.value);

                isTextField = true;

                bodyPane.getChildren().add(textField);
            }
            case TEXTAREA -> {
                textArea = new TextArea();
                textArea.getStyleClass().addAll("prompt-text",type.getValue());
                textArea.setPromptText(this.placeHolder);
                textArea.setText(this.value);

                isTextField = false;

                bodyPane.getChildren().add(textArea);
            }
        }

        // add to the parent
        parent.getChildren().add(bodyPane);
    }
    // create the MyNotifier Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        HBox buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("prompt-footer");
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

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
                if (actionObj instanceof Runnable action) {
                    button.setOnAction(e -> {
                        action.run();
                        close();
                    });
                }
                else if (actionObj instanceof Consumer) {
                    Consumer<String> action = (Consumer<String>) actionObj;
                    button.setOnAction(e -> {
                        action.accept(getUserInput());
                        close();
                    });
                }
                else {
                    Runnable defaultAction = () -> System.out.println(label + " clicked");
                    button.setOnAction(e -> {
                        defaultAction.run();
                        close();
                    });
                }

                buttonContainer.getChildren().add(button);
            }
            else {
                System.err.println("Properties map is null for button: " + label);
            }
        });

        parent.getChildren().add(buttonContainer);
    }
    // Auto closing the function
    private void autoClosePrompt(Boolean autoClose, double duration) {
        if (!autoClose) return;

        NotifyUtils.timeOut(this::run,duration == 0? this.durationInSeconds:duration);
    }
    private void run() {
        close();
    }
    private String getUserInput() {
        if (isTextField) return textField.getText().trim();

        return textArea.getText().trim();
    }
    private VBox parent() {
        VBox vBox = (VBox) getLayout();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("prompt","parent-container");

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
    public void create() {
        // create prompt pane
        VBox vBox = parent();

        // create the head
        createHead(vBox, headerText);

        // create the body
        createBody(vBox);

        // create footer
        createFooter(vBox, buttons);

        // showing the prompt
        show(vBox,position,animation,durationInSeconds);

        // create the close function
        autoClosePrompt(autoClose, durationInSeconds);
    }

}
