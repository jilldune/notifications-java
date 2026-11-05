package com.octalinc.notificationsjava.Notify;

import com.octalinc.notificationsjava.Managers.NotifyBase;
import com.octalinc.notificationsjava.NotifierUtil.NotifyPos;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Prompt extends NotifyBase {

    // ---------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------

    private String headerText = "Prompt";
    private String message = "Prompt body text";

    private NotifyPos position = NotifyPos.CENTER;

    private double animationDuration = 0.5;
    private double displayDuration = 0.5;

    private boolean autoClose = false;
    private boolean animation = true;

    private final Map<String, Map<String, Object>> buttons =
            new LinkedHashMap<>();

    public Prompt(Stage stage) {
        super(stage, new VBox());
    }

    // ---------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------

    private void createHead(VBox parent, String headerText) {

        Label header = new Label(headerText);

        header.setTextOverrun(OverrunStyle.ELLIPSIS);
        header.getStyleClass().add("prompt-title");

        HBox head = head(header);

        head.getStyleClass().add("prompt-head");

        parent.getChildren().add(head);
    }

    private HBox head(Label header) {

        BorderPane headerBorderPane = new BorderPane();

        headerBorderPane.setBackground(Background.EMPTY);

        // Close icon
        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);

        icon.getStyleClass().add("icon");

        Button btnIcon = new Button();

        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);

        btnIcon.getStyleClass().addAll(
                "prompt-close",
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
    // BODY
    // ---------------------------------------------------------

    private void createBody(VBox parent) {

        parent.getChildren().add(
                getTextArea(message)
        );
    }

    private TextArea getTextArea(String bodyText) {

        TextArea messageArea = new TextArea();

        messageArea.setText(bodyText);
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        messageArea.setFocusTraversable(false);
        messageArea.setCursor(Cursor.DEFAULT);

        messageArea.getStyleClass().add("prompt-body");

        return messageArea;
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

        footerContainer.getStyleClass().add("prompt-footer");
        footerContainer.setAlignment(Pos.CENTER);

        HBox buttonContainer = new HBox();

        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        buttonContainer.getStyleClass().add("button-container");

        buttons.forEach((label, properties) -> {

            if (properties == null) {
                return;
            }

            Button button = new Button(label);

            // Optional button style
            Object styleObj = properties.get("style");

            if (styleObj instanceof String styles &&
                    !styles.isBlank()) {

                button.setStyle(styles);
            }

            button.getStyleClass().add("button");

            // Button action
            Object actionObj = properties.get("action");

            if (actionObj instanceof Runnable action) {

                button.setOnAction(event ->
                        close(action)
                );

            } else if (actionObj instanceof Consumer<?> consumer) {

                @SuppressWarnings("unchecked")
                Consumer<String> action =
                        (Consumer<String>) consumer;

                /*
                 * The Consumer receives the current prompt message.
                 *
                 * The callback executes after the prompt closes,
                 * following the NotifyBase close lifecycle.
                 */
                String promptMessage = message;

                button.setOnAction(event ->
                        close(() -> action.accept(promptMessage))
                );

            } else {

                // No callback supplied: simply close the prompt.
                button.setOnAction(event -> close());
            }

            buttonContainer.getChildren().add(button);
        });

        footerContainer.getChildren().add(buttonContainer);

        parent.getChildren().add(footerContainer);
    }

    // ---------------------------------------------------------
    // ROOT CONTAINER
    // ---------------------------------------------------------

    private VBox parent() {

        VBox vBox = (VBox) getLayout();

        vBox.setAlignment(Pos.CENTER);

        vBox.getStyleClass().addAll(
                "prompt",
                "parent-container"
        );

        return vBox;
    }

    // ---------------------------------------------------------
    // PUBLIC CONFIGURATION API
    // ---------------------------------------------------------

    public Prompt setHeader(String header) {

        this.headerText = header;

        return this;
    }

    public Prompt setBody(String message) {

        this.message = message;

        return this;
    }

    public Prompt setPosition(NotifyPos position) {

        this.position = position;

        return this;
    }

    /*
     * Controls entrance and exit animation duration.
     */
    public Prompt setDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.animationDuration = durationInSeconds;

        return this;
    }

    /*
     * Controls how long the prompt remains visible
     * when auto-close is enabled.
     */
    public Prompt setDisplayDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.displayDuration = durationInSeconds;

        return this;
    }

    public Prompt autoClose(Boolean autoClose) {

        this.autoClose = Boolean.TRUE.equals(autoClose);

        return this;
    }

    public Prompt setAnimation(Boolean animation) {

        this.animation = Boolean.TRUE.equals(animation);

        return this;
    }

    // ---------------------------------------------------------
    // BUTTON CONFIGURATION
    // ---------------------------------------------------------

    public Prompt setButton(
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

    public Prompt setButton(
            String label,
            Runnable action
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);

        buttons.put(label, properties);

        return this;
    }

    public Prompt setButton(
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

        if (headerText == null || message == null) {
            throw new IllegalStateException(
                    "Prompt header and message cannot be null."
            );
        }

        VBox vBox = parent();

        createHead(vBox, headerText);

        createBody(vBox);

        createFooter(vBox, buttons);

        /*
         * Submit the fully configured prompt
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