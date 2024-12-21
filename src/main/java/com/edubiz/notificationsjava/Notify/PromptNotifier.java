package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.NotifierUtil.Helper;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class PromptNotifier extends BaseNotifier {
    private TextArea textArea;
    private TextField textField;

    //    class variables
    private static boolean isTextField = true;
    private String headerText = "Prompt";
    private NotifierInputType type = NotifierInputType.TEXT;
    private String value = "";
    private String placeHolder = "";
    private String label = null;
    private NotificationPos position = NotificationPos.CENTER;
    private double durationInSeconds = 3.5;
    private Boolean autoClose = true;
    private Boolean animation = true;
    private final Map<String,Map<String,Object>> buttons = new LinkedHashMap<>();

    public PromptNotifier(Stage stage) {
        super(stage);
    }

    public void prompt(Map<String, Object> options) {
        String header = (String) options.getOrDefault("header", "NotifierPrompt");
        Double duration = (Double) options.getOrDefault("duration", 3.5);
        Boolean animation = (Boolean) options.getOrDefault("animation", true);
        Boolean autoClose = (Boolean) options.getOrDefault("autoClose", true);
        NotificationPos position = (NotificationPos) options.getOrDefault("position", "center");

        // create child pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("prompt","parent-container");

        // create the head
        createHead(vBox, header);

        // create the body
        createBody(vBox, options);

        // create footer
        if (options.containsKey("buttons")) {
            Map<String, Map<String, Object>> buttons = (Map<String, Map<String, Object>>) options.getOrDefault("buttons", Map.of());
            createFooter(vBox, buttons);
        }

        show(vBox,position,animation,duration);

        // create the close function
        autoClosePrompt(autoClose, duration);
    }

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
        btnIcon.getStyleClass().add("prompt-close");

        btnIcon.setOnMouseClicked(event -> close());

        // create the head container
        HBox head = new HBox(header,btnIcon);// Add label and close icon to the head
        head.setAlignment(Pos.CENTER_LEFT);

        return head;
    }

    // create MyNotifier message body
    private void createBody(VBox parent, Map<String, Object> options) {
        NotifierInputType inputType = (NotifierInputType) options.getOrDefault("type", this.type);
        String placeHolder = (String) options.getOrDefault("placeHolder", this.placeHolder);
        String value = (String) options.getOrDefault("value", this.value);
        String label = (String) options.getOrDefault("label", this.label);

        // create a body container
        VBox bodyPane = new VBox();
        bodyPane.getStyleClass().add("prompt-body");

        // create label
        if (label != null) {
            Label inputLabel = new Label();
            inputLabel.setText(label);
            inputLabel.setTextAlignment(TextAlignment.LEFT);
            inputLabel.getStyleClass().add("prompt-label");

            bodyPane.getChildren().add(inputLabel);
        }

        // Create the input
        switch(inputType) {
            case TEXT -> {
                textField = new TextField();
                textField.getStyleClass().addAll("prompt-text",type.getValue());
                textField.setPromptText(placeHolder);
                textField.setText(value);

                isTextField = true;

                bodyPane.getChildren().add(textField);
            }
            case TEXTAREA -> {
                textArea = new TextArea();
                textArea.getStyleClass().addAll("prompt-text",type.getValue());
                textArea.setPromptText(placeHolder);
                textArea.setText(value);

                isTextField = false;

                bodyPane.getChildren().add(textArea);
            }
        }

        // add to the parent
        parent.getChildren().add(bodyPane);
    }
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

            bodyPane.getChildren().add(inputLabel);
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
            Consumer<String> action = (Consumer<String>) properties.getOrDefault("action", (Consumer<String>) (String text) -> System.out.println(label + " clicked"));

            button.setOnAction(e -> {
                action.accept(getUserInput());
                close();
            });

            buttonContainer.getChildren().add(button);
        });

        parent.getChildren().add(buttonContainer);
    }

    // Auto closing the function
    private void autoClosePrompt(Boolean autoClose, double duration) {
        if (!autoClose) return;

        Helper.timeOut(this::run,duration == 0? 3500:duration);
    }

    private void run() {
        close();
    }

    public String getUserInput() {
        if (isTextField) return textField.getText().trim();

        return textArea.getText().trim();
    }
    

    
//    Plain Methods
    public void setHeader(String header) {
        headerText = header;
    }
    public void setType(NotifierInputType type) {
        this.type = type;
    }
    public void setPlaceholder(String placeHolder) {
        this.placeHolder = placeHolder;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setLabel(String label) {
        this.label = label;
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
    public void setButton(String label,Consumer<String> action,String style) {
        this.buttons.put(label,Map.of(
                "action",action,
                "style",style
        ));
    }
    public void setButton(String label,Consumer<String> action) {
        this.buttons.put(label,Map.of("action",action));
    }
    public void create() {
        // create prompt pane
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().addAll("prompt","parent-container");

        // create the head
        createHead(vBox, headerText);

        // create the body
        createBody(vBox);

        // create footer
        createFooter(vBox, buttons);

        show(vBox,position,animation,durationInSeconds);

        // create the close function
        autoClosePrompt(autoClose, durationInSeconds);
    }

}
