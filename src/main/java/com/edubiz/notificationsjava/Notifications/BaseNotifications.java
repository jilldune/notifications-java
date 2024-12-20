package com.edubiz.notificationsjava.Notifications;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public abstract class BaseNotifications {
    protected Stage stage;
    protected AnchorPane notificationRoot;

    public BaseNotifications(Stage stage) {
        // Init the notification root Pane
        notificationRoot = new AnchorPane();
        notificationRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/notification-styles.css")).toExternalForm());
        notificationRoot.getStyleClass().add("root");

        Scene scene = stage.getScene();
        Parent userRoot = scene.getRoot();

        if (userRoot instanceof Pane) {
            ((Pane) userRoot).getChildren().add(notificationRoot);
        } else if (userRoot instanceof Group) {
            ((Group) userRoot).getChildren().add(notificationRoot);
        } else {
            // wrap in a stack pane if none specified
            StackPane newRoot = new StackPane();
            scene.setRoot(newRoot);
        }

        notificationRoot.prefWidthProperty().bind(scene.widthProperty());
        notificationRoot.prefHeightProperty().bind(scene.heightProperty());

        this.stage = stage;
    }

    protected void setPosition(Node node,NotificationPos position) {
        getPosition(node,position);
    }

    protected void getPosition(Node node,NotificationPos position) {
        // Process the notification position first
        String nPos = "";
        switch (position) {
            case TOP,TOP_LEFT,TOP_RIGHT,LEFT,RIGHT,BOTTOM,BOTTOM_LEFT,BOTTOM_RIGHT,CENTER ->  nPos = position.getPosition();
        };

        String[] positions = nPos.toLowerCase().split("-");
        Double top = null,left = null,right = null,bottom = null;

        for (String pos : positions) {
            switch (pos) {
                case "top": top = 10.0; break;
                case "right": right = 10.0; break;
                case "left": left = 10.0; break;
                case "bottom": bottom = 10.0; break;
                case "center": centerNode(node); return;
                default: throw new IllegalArgumentException("Invalid position: "+pos);
            }
        }

        applyPosition(node,top,left,right,bottom);
    }

    // Center the notification at the center of the window
    protected void centerNode(Node node) {
        AnchorPane parent = (AnchorPane) node.getParent();

        if (parent == null) throw new IllegalStateException("Node must be added to a parent AnchorPane first");

        parent.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            double centerX = (newBounds.getWidth() - node.prefWidth(-1)) / 2;
            double centerY = (newBounds.getHeight() - node.prefHeight(-1)) / 2;

            AnchorPane.setLeftAnchor(node,centerX);
            AnchorPane.setTopAnchor(node,centerY);
        });
    }

    // creates or applies the positions to the notification
    protected void applyPosition(Node node,Double top,Double left,Double right,Double bottom) {
        if (top != null) AnchorPane.setTopAnchor(node,top);
        if (left != null) AnchorPane.setLeftAnchor(node,left);
        if (right != null) AnchorPane.setRightAnchor(node,right);
        if (bottom != null) AnchorPane.setBottomAnchor(node,bottom);
    }

    // Method for displaying any type of notification
    public void show(Node node,NotificationPos position) {
        if (! notificationRoot.getChildren().contains(node))
            notificationRoot.getChildren().add(node);

        notificationRoot.setVisible(true);

        // Setting the position of the notification type
        if (position != null) setPosition(node,position);
    }

    public void close() {
        notificationRoot.getChildren().clear();
        notificationRoot.setVisible(false);
    }
}
