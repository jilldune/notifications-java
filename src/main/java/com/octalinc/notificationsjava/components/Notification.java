package com.octalinc.notificationsjava.components;

import com.octalinc.notificationsjava.core.NotifyBase;
import com.octalinc.notificationsjava.enums.NotifyAlert;
import com.octalinc.notificationsjava.enums.NotifyPos;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;

public class Notification extends NotifyBase {

    // ---------------------------------------------------------
    // CONFIGURATION
    // ---------------------------------------------------------

    private static final double DEFAULT_ANIMATION_DURATION = 0.5;
    private static final double DEFAULT_DISPLAY_DURATION = 0.5;

    private String headerText = "Notifier";
    private String body = "";

    private double animationDuration = DEFAULT_ANIMATION_DURATION;
    private double displayDuration = DEFAULT_DISPLAY_DURATION;

    private NotifyPos position = NotifyPos.CENTER;

    private boolean autoClose = false;
    private boolean animation = true;

    private NotifyAlert alertType = NotifyAlert.NEUTRAL;

    private final Map<String, Map<String, Object>> buttons =
            new LinkedHashMap<>();

    public Notification(Stage stage) {
        super(stage, new VBox());
    }

    // ---------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------

    private void createHead(VBox parent, String headerText) {

        Label header = new Label(headerText);

        String colorStyle = alertType.getTypeName();
        Ikon iconCode = getIkon(alertType);

        if (alertType != NotifyAlert.NEUTRAL) {

            FontIcon icon = new FontIcon(iconCode);

            icon.getStyleClass().add("icon");

            header.setGraphic(icon);
            header.setGraphicTextGap(2.0);
        }

        header.setTextOverrun(OverrunStyle.ELLIPSIS);

        header.getStyleClass().addAll(
                "notification-title",
                colorStyle
        );

        HBox head = getHead(header);

        head.getStyleClass().add("notification-head");

        parent.getChildren().add(head);
    }

    private HBox getHead(Label header) {

        BorderPane headerBorderPane = new BorderPane();

        headerBorderPane.setBackground(Background.EMPTY);

        // Close icon
        FontIcon icon = new FontIcon(RemixiconAL.CLOSE_FILL);

        icon.getStyleClass().add("icon");

        Button btnIcon = new Button();

        btnIcon.setGraphic(icon);
        btnIcon.setAlignment(Pos.CENTER);

        btnIcon.getStyleClass().addAll(
                "notification-close",
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

    private void createBody(VBox parent, String bodyText) {

        parent.getChildren().add(
                getBody(bodyText)
        );
    }

    private AnchorPane getBody(String bodyText) {

        AnchorPane bodyContainer = new AnchorPane();

        TextArea messageArea = new TextArea();

        messageArea.setText(bodyText);
        messageArea.setWrapText(true);
        messageArea.setEditable(false);
        messageArea.setFocusTraversable(false);
        messageArea.setCursor(Cursor.DEFAULT);

        messageArea.getStyleClass().add("notification-body");

        AnchorPane.setTopAnchor(messageArea, 0.0);
        AnchorPane.setRightAnchor(messageArea, 0.0);
        AnchorPane.setBottomAnchor(messageArea, 0.0);
        AnchorPane.setLeftAnchor(messageArea, 0.0);

        bodyContainer.getChildren().add(messageArea);

        return bodyContainer;
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

        footerContainer.getStyleClass().add("notification-footer");

        HBox buttonContainer = new HBox();

        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        buttonContainer.getStyleClass().add("button-container");

        buttons.forEach((label, properties) -> {

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

            // Optional icon
            Object iconObject = properties.get("icon");

            if (iconObject instanceof String iconUrl &&
                    !iconUrl.isBlank()) {

                Image image = new Image(iconUrl, true);

                ImageView icon = new ImageView(image);

                icon.setFitWidth(16);
                icon.setFitHeight(16);

                button.setGraphic(icon);
            }

            // Button action
            Object actionObject = properties.get("action");

            if (actionObject instanceof Runnable action) {

                button.setOnAction(event -> close(action));

            } else {

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
                "notification",
                "parent-container"
        );

        return vBox;
    }

    // ---------------------------------------------------------
    // ICON MAPPING
    // ---------------------------------------------------------

    private static Ikon getIkon(NotifyAlert type) {

        return switch (type) {

            case SUCCESS ->
                    RemixiconAL.CHECKBOX_CIRCLE_FILL;

            case ERROR ->
                    RemixiconAL.CLOSE_CIRCLE_FILL;

            case ALERT ->
                    RemixiconAL.ALERT_FILL;

            case NEUTRAL ->
                    RemixiconAL.INDETERMINATE_CIRCLE_FILL;

            case INFO ->
                    RemixiconAL.INFORMATION_FILL;
        };
    }

    // ---------------------------------------------------------
    // PUBLIC CONFIGURATION API
    // ---------------------------------------------------------

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

    /*
     * Controls entrance and exit animation duration.
     */
    public Notification setDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.animationDuration = durationInSeconds;

        return this;
    }

    /*
     * Controls how long the notification remains visible
     * when auto-close is enabled.
     */
    public Notification setDisplayDuration(Double durationInSeconds) {

        validateDuration(durationInSeconds);

        this.displayDuration = durationInSeconds;

        return this;
    }

    public Notification setAlertType(NotifyAlert type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Notification alert type cannot be null."
            );
        }

        this.alertType = type;

        return this;
    }

    public Notification autoClose(Boolean autoClose) {

        this.autoClose = Boolean.TRUE.equals(autoClose);

        return this;
    }

    public Notification setAnimation(Boolean animation) {

        this.animation = Boolean.TRUE.equals(animation);

        return this;
    }

    // ---------------------------------------------------------
    // BUTTON CONFIGURATION
    // ---------------------------------------------------------

    public Notification setButton(
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

    public Notification setButton(
            String label,
            Runnable action
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);

        buttons.put(label, properties);

        return this;
    }

    /*
     * Allows a button to have an icon URL.
     */
    public Notification setButton(
            String label,
            Runnable action,
            String style,
            String iconUrl
    ) {

        Map<String, Object> properties = new LinkedHashMap<>();

        properties.put("action", action);
        properties.put("style", style);
        properties.put("icon", iconUrl);

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

        if (headerText == null || body == null) {

            throw new IllegalStateException(
                    "Notification header and body cannot be null."
            );
        }

        VBox vBox = parent();

        createHead(vBox, headerText);

        createBody(vBox, body);

        createFooter(vBox, buttons);

        /*
         * Submit the fully configured notification
         * to the shared lifecycle.
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