package com.octalinc.notificationsjava.components;

import com.octalinc.notificationsjava.core.NotifyBase;
import com.octalinc.notificationsjava.enums.NotifyAlert;
import com.octalinc.notificationsjava.enums.NotifyPos;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

public class Toast extends NotifyBase {

    private static final double DEFAULT_DISPLAY_DURATION = 0.7;
    private static final double DEFAULT_ANIMATION_DURATION = 0.5;

    private static final NotifyPos DEFAULT_POSITION = NotifyPos.TOP;

    public Toast(Stage stage) {
        super(stage, new HBox());
    }

    // ---------------------------------------------------------
    // PUBLIC CREATE METHODS
    // ---------------------------------------------------------

    public void create(NotifyAlert type, String message) {
        createToast(
                type,
                message,
                DEFAULT_POSITION,
                true,
                DEFAULT_DISPLAY_DURATION
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            NotifyPos position
    ) {
        createToast(
                type,
                message,
                position,
                true,
                DEFAULT_DISPLAY_DURATION
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            Boolean animation
    ) {
        createToast(
                type,
                message,
                DEFAULT_POSITION,
                Boolean.TRUE.equals(animation),
                DEFAULT_DISPLAY_DURATION
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            double durationInSeconds
    ) {
        createToast(
                type,
                message,
                DEFAULT_POSITION,
                true,
                durationInSeconds
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            NotifyPos position,
            boolean animation
    ) {
        createToast(
                type,
                message,
                position,
                animation,
                DEFAULT_DISPLAY_DURATION
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            NotifyPos position,
            double durationInSeconds
    ) {
        createToast(
                type,
                message,
                position,
                true,
                durationInSeconds
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            boolean animation,
            double durationInSeconds
    ) {
        createToast(
                type,
                message,
                DEFAULT_POSITION,
                animation,
                durationInSeconds
        );
    }

    public void create(
            NotifyAlert type,
            String message,
            NotifyPos position,
            boolean animation,
            double durationInSeconds
    ) {
        createToast(
                type,
                message,
                position,
                animation,
                durationInSeconds
        );
    }

    // ---------------------------------------------------------
    // TOAST CREATION
    // ---------------------------------------------------------

    private void createToast(
            NotifyAlert type,
            String message,
            NotifyPos position,
            boolean animation,
            double displayDuration
    ) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Toast notification type cannot be null."
            );
        }

        if (position == null) {
            throw new IllegalArgumentException(
                    "Toast position cannot be null."
            );
        }

        if (displayDuration < 0 ||
                !Double.isFinite(displayDuration)) {
            throw new IllegalArgumentException(
                    "Display duration must be a finite, non-negative value."
            );
        }

        // -----------------------------------------------------
        // TOAST CONFIGURATION
        // -----------------------------------------------------

        Ikon iconCode = getIkon(type);
        String colorStyle = type.getTypeName();

        // -----------------------------------------------------
        // TOAST CONTAINER
        // -----------------------------------------------------

        HBox hBox = (HBox) getLayout();

        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.getStyleClass().addAll(
                "toast",
                "parent-container",
                colorStyle
        );

        // -----------------------------------------------------
        // ICON
        // -----------------------------------------------------

        FontIcon icon = new FontIcon(iconCode);

        icon.getStyleClass().add("icon");

        Label iconWrapper = new Label();

        iconWrapper.setGraphic(icon);
        iconWrapper.setAlignment(Pos.CENTER);

        iconWrapper.getStyleClass().add("toast-icon");

        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        Label text = new Label(message);

        text.getStyleClass().add("toast-message");

        text.setWrapText(true);
        text.setTextAlignment(TextAlignment.LEFT);

        // -----------------------------------------------------
        // ASSEMBLE COMPONENT
        // -----------------------------------------------------

        hBox.getChildren().addAll(
                iconWrapper,
                text
        );

        // -----------------------------------------------------
        // SUBMIT TO NOTIFICATION LIFECYCLE
        // -----------------------------------------------------

        show(
                hBox,
                position,
                animation,
                DEFAULT_ANIMATION_DURATION,
                true,
                displayDuration
        );
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
}
