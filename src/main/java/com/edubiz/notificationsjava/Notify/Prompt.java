package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.Managers.NotifyBase;
import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.remixicon.RemixiconAL;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Prompt extends NotifyBase {
    //    class variables
    private String headerText = "Prompt";
    private String message = "Prompt body text";
    private NotifyPos position = NotifyPos.CENTER;
    private double durationInSeconds = 4.5;
    private Boolean autoClose = true;
    private Boolean animation = true;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();

    public Prompt(Stage stage) { super(stage, new VBox()); }

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
        // add to the parent
        parent.getChildren().add(getTextArea(this.message));
    }
    @NotNull
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

    // create the MyNotifier Footer
    private void createFooter(VBox parent,Map<String,Map<String,Object>> buttons) {
        if (buttons == null) return;

        // main footer container
        VBox footerContainer = new VBox();
        footerContainer.getStyleClass().add("prompt-footer");
        footerContainer.setAlignment(Pos.CENTER);

        // BUTTON CONTAINER
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(5);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.getStyleClass().add("button-container");

        // CREATE/ADD BUTTONS
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

        // ADD BUTTONS TO THE FOOTER
        footerContainer.getChildren().add(buttonContainer);

        parent.getChildren().add(footerContainer);
    }
    // Auto closing the function
    private void autoClosePrompt(Boolean autoClose, double duration) {
        if (!autoClose) return;

        NotifyUtils.timeOut(this::run,duration == 0? this.durationInSeconds:duration);
    }
    private void run() {
        close();
    }
    private VBox parent() {
        VBox vBox = (VBox) getLayout();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("prompt","parent-container");

        return vBox;
    }
    
    // ========== Exposed creator Methods ==========

    public Prompt setHeader(String header) {
        headerText = header;
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
    public Prompt setDuration(Double durationInSeconds) {
        this.durationInSeconds = durationInSeconds < .7? this.durationInSeconds:durationInSeconds;
        return this;
    }
    public Prompt autoClose(Boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }
    public Prompt setAnimation(Boolean animation) {
        this.animation = animation;
        return this;
    }
    public Prompt setButton(String label, Consumer<String> action, String style) {
        this.buttons.put(label,Map.of("action",action,"style",style));
        return this;
    }
    public Prompt setButton(String label, Runnable action) {
        this.buttons.put(label,Map.of("action",action));
        return this;
    }
    public Prompt setButton(String label, Runnable action, String style) {
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
