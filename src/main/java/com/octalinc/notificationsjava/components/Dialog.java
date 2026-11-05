package com.octalinc.notificationsjava.components;

import com.octalinc.notificationsjava.core.NotifyBase;
import com.octalinc.notificationsjava.enums.NotifyPos;
import com.octalinc.notificationsjava.enums.NotifyInput;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Dialog extends NotifyBase {

    // ---------------------------------------------------------
    // INPUT CONTROLS
    // ---------------------------------------------------------

    private TextArea textArea;
    private TextField textField;
    private PasswordField passwordField;

    // ---------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------

    private String field = NotifyInput.TEXT_FIELD.getValue();

    private String headerText = "Dialog";

    private NotifyInput type = NotifyInput.TEXT_FIELD;

    private String value = "";
    private String placeHolder = "";
    private String label = null;

    private NotifyPos position = NotifyPos.CENTER;

    private double animationDuration = 0.5;
    private double displayDuration = 0.5;

    private boolean autoClose = false;
    private boolean animation = true;

    private final Map<String, Map<String, Object>> buttons =
            new LinkedHashMap<>();

    private boolean bindInputAction = false;

    private Integer buttonIndex = null;

    public Dialog(Stage stage) {
        super(stage, new VBox());
    }

    // ---------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------

    private void createHead(VBox parent, String headerText) {

        Label header = new Label(headerText);

        header.setTextOverrun(OverrunStyle.ELLIPSIS);

        header.getStyleClass().add("dialog-title");

        HBox head = head(header);

        head.getStyleClass().add("dialog-head");

        parent.getChildren().add(head);
    }

    private HBox head(Label header) {

        BorderPane headerBorderPane = new BorderPane();

        headerBorderPane.setBackground(Background.EMPTY);

        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);

        icon.getStyleClass().add("icon");

        Button btnIcon = new Button();

        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);

        btnIcon.getStyleClass().addAll(
                "dialog-close",
                "close-btn"
        );

        btnIcon.setOnAction(event -> close());

        // Header wrapper
        AnchorPane wrapper = new AnchorPane();

        wrapper.setPrefWidth(500);

        AnchorPane.setTopAnchor(header, 0.0);
        AnchorPane.setLeftAnchor(header, 0.0);
        AnchorPane.setBottomAnchor(header, 0.0);

        wrapper.getChildren().add(header);

        headerBorderPane.setCenter(wrapper);
        headerBorderPane.setRight(btnIcon);

        HBox head = new HBox(headerBorderPane);

        head.setAlignment(Pos.CENTER_LEFT);

        return head;
    }

    // ---------------------------------------------------------
    // BODY / INPUT
    // ---------------------------------------------------------

    private void createBody(VBox parent) {

        VBox bodyPane = new VBox();

        bodyPane.getStyleClass().add("dialog-body");

        // Optional input label
        if (label != null && !label.isBlank()) {

            Label inputLabel = new Label(label);

            inputLabel.setTextAlignment(TextAlignment.LEFT);

            inputLabel.getStyleClass().add("dialog-label");

            VBox divider = new VBox();

            divider.getStyleClass().add("dialog-divider");

            bodyPane.getChildren().addAll(
                    inputLabel,
                    divider
            );
        }

        switch (type) {

            case TEXT_FIELD -> {

                textField = new TextField();

                textField.getStyleClass().addAll(
                        "dialog-text",
                        type.getValue()
                );

                textField.setPromptText(placeHolder);
                textField.setText(value);

                field = type.getValue();

                bodyPane.getChildren().add(textField);
            }

            case PASSWORD_FIELD -> {

                passwordField = new PasswordField();

                passwordField.getStyleClass().addAll(
                        "dialog-text",
                        type.getValue()
                );

                passwordField.setPromptText(placeHolder);
                passwordField.setText(value);

                field = type.getValue();

                bodyPane.getChildren().add(passwordField);
            }

            case TEXTAREA -> {

                textArea = new TextArea();

                textArea.getStyleClass().addAll(
                        "dialog-text",
                        type.getValue()
                );

                textArea.setPromptText(placeHolder);
                textArea.setWrapText(true);
                textArea.setText(value);

                field = type.getValue();

                bodyPane.getChildren().add(textArea);
            }
        }

        parent.getChildren().add(bodyPane);
    }

    // ---------------------------------------------------------
    // INPUT ACTION BINDING
    // ---------------------------------------------------------

    private void bindInputAction(
            int index,
            EventHandler<ActionEvent> handler
    ) {

        if (!bindInputAction || buttonIndex == null) {
            return;
        }

        if (buttonIndex != index) {
            return;
        }

        Node input = getInput();

        if (input instanceof TextField textField) {

            textField.setOnAction(handler);

        } else if (input instanceof PasswordField passwordField) {

            passwordField.setOnAction(handler);

        } else if (input instanceof TextArea textArea) {

            textArea.setOnKeyPressed(event -> {

                if (event.getCode() == KeyCode.ENTER &&
                        event.isControlDown()) {

                    handler.handle(new ActionEvent());
                }
            });
        }
    }

    // ---------------------------------------------------------
    // FOOTER / BUTTONS
    // ---------------------------------------------------------

    private void createFooter(
            VBox parent,
            Map<String, Map<String, Object>> buttons
    ) {

        if (buttons == null || buttons.isEmpty()) {
            return;
        }

        VBox footerContainer = new VBox();

        footerContainer.getStyleClass().add("dialog-footer");

        HBox buttonContainer = new HBox(5);

        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setMinWidth(Region.USE_PREF_SIZE);
        buttonContainer.setFillHeight(true);

        buttonContainer.getStyleClass().add("button-container");

        AtomicInteger index = new AtomicInteger(0);

        buttons.forEach((label, properties) -> {

            int currentIndex = index.getAndIncrement();

            if (properties == null) {
                return;
            }

            Button button = new Button(label);

            // Optional style
            Object styleObject = properties.get("style");

            if (styleObject instanceof String styles &&
                    !styles.isBlank()) {

                button.setStyle(styles);
            }

            button.getStyleClass().add("button");

            // Button action
            Object actionObject = properties.get("action");

            EventHandler<ActionEvent> handler =
                    getActionEventHandler(actionObject);

            button.setOnAction(handler);

            // Optional input keyboard binding
            bindInputAction(currentIndex, handler);

            buttonContainer.getChildren().add(button);
        });

        footerContainer.getChildren().add(buttonContainer);

        parent.getChildren().add(footerContainer);
    }

    // ---------------------------------------------------------
    // BUTTON ACTION HANDLER
    // ---------------------------------------------------------

    @SuppressWarnings("unchecked")
    private EventHandler<ActionEvent> getActionEventHandler(
            Object actionObject
    ) {

        if (actionObject instanceof Runnable action) {

            return event -> close(action);
        }

        if (actionObject instanceof Consumer<?> consumer) {

            Consumer<String> action =
                    (Consumer<String>) consumer;

            return event -> {

                /*
                 * Capture the input when the user activates
                 * the button, before the dialog closes.
                 */
                String input = getUserInput();

                close(() -> action.accept(input));
            };
        }

        return event -> close();
    }

    // ---------------------------------------------------------
    // INPUT VALUE
    // ---------------------------------------------------------

    private String getUserInput() {

        return switch (field.toLowerCase()) {

            case "text" ->
                    textField.getText().trim();

            case "password" ->
                    passwordField.getText();

            case "textarea" ->
                    textArea.getText().trim();

            default ->
                    throw new IllegalStateException(
                            "Unexpected input type: " + field
                    );
        };
    }

    private Node getInput() {

        return switch (field.toLowerCase()) {

            case "text" ->
                    textField;

            case "password" ->
                    passwordField;

            case "textarea" ->
                    textArea;

            default ->
                    throw new IllegalStateException(
                            "Unexpected input type: " + field
                    );
        };
    }

    // ---------------------------------------------------------
    // ROOT CONTAINER
    // ---------------------------------------------------------

    private VBox parent() {

        VBox vBox = (VBox) getLayout();

        vBox.setAlignment(Pos.CENTER);

        vBox.getStyleClass().addAll(
                "dialog",
                "parent-container"
        );

        return vBox;
    }

    // ---------------------------------------------------------
    // PUBLIC CONFIGURATION API
    // ---------------------------------------------------------

    public Dialog setHeader(String header) {

        this.headerText = header;

        return this;
    }

    public Dialog setType(NotifyInput type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Dialog input type cannot be null."
            );
        }

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

        if (position == null) {
            throw new IllegalArgumentException(
                    "Dialog position cannot be null."
            );
        }

        this.position = position;

        return this;
    }

    /*
     * Controls entrance and exit animation duration.
     */
    public Dialog setDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.animationDuration = durationInSeconds;

        return this;
    }

    /*
     * Controls how long the dialog remains visible
     * when auto-close is enabled.
     */
    public Dialog setDisplayDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.displayDuration = durationInSeconds;

        return this;
    }

    public Dialog autoClose(Boolean autoClose) {

        this.autoClose = Boolean.TRUE.equals(autoClose);

        return this;
    }

    public Dialog setAnimation(Boolean animation) {

        this.animation = Boolean.TRUE.equals(animation);

        return this;
    }

    // ---------------------------------------------------------
    // BUTTON CONFIGURATION
    // ---------------------------------------------------------

    public Dialog setButton(
            String label,
            Consumer<String> action,
            String style
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);
        properties.put("style", style);

        buttons.put(label, properties);

        return this;
    }

    public Dialog setButton(
            String label,
            Consumer<String> action
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);

        buttons.put(label, properties);

        return this;
    }

    public Dialog setButton(
            String label,
            Runnable action
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);

        buttons.put(label, properties);

        return this;
    }

    public Dialog setButton(
            String label,
            Runnable action,
            String style
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);
        properties.put("style", style);

        buttons.put(label, properties);

        return this;
    }

    // ---------------------------------------------------------
    // KEYBOARD BINDING CONFIGURATION
    // ---------------------------------------------------------

    public Dialog bindInputAction(int buttonIndex) {

        if (buttonIndex < 0) {
            throw new IllegalArgumentException(
                    "Button index cannot be negative."
            );
        }

        this.bindInputAction = true;
        this.buttonIndex = buttonIndex;

        return this;
    }

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    private void validateDuration(Double duration) {

        if (duration == null ||
                !Double.isFinite(duration) ||
                duration < 0) {

            throw new IllegalArgumentException(
                    "Duration must be a finite, non-negative value."
            );
        }
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    public void create() {

        if (headerText == null || value == null ||
                placeHolder == null) {

            throw new IllegalStateException(
                    "Dialog header, value and placeholder cannot be null."
            );
        }

        // Create dialog container
        VBox vBox = parent();

        // Header
        createHead(vBox, headerText);

        // Input body
        createBody(vBox);

        // Footer
        createFooter(vBox, buttons);

        /*
         * Submit the fully configured dialog
         * to the shared notification lifecycle.
         */
        show(
                vBox,
                position,
                animation,
                animationDuration,
                autoClose,
                displayDuration
        );
    }
}