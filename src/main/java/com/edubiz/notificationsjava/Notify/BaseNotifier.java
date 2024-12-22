package com.edubiz.notificationsjava.Notify;

import com.edubiz.notificationsjava.NotifierUtil.Helper;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

public abstract class BaseNotifier {
    protected Stage stage;
    protected AnchorPane notificationRoot;

    public BaseNotifier(Stage stage) {
        // Init the notification root Pane
        notificationRoot = new AnchorPane();
        notificationRoot.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/_notify-styles/notification-styles.css")).toExternalForm());
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

    // creates or applies the positions to the notification
    protected void setInanimatePosition(Node node,NotificationPos position) {
        Scene scene = stage.getScene();
        Helper helper = new Helper();
        // get geometry
        Map<String, Double> helperGeometry = helper.getGeometry(node,scene);

        // Set bounds
        double width = helperGeometry.get("width");
        double height = helperGeometry.get("height");

        // Get the scene bounds
        double sceneWidth = helperGeometry.get("sceneWidth");
        double sceneHeight = helperGeometry.get("sceneHeight");

        // Define positioning
        Map<String, Object> coordinate = helper.parsePosition(position,width,height,sceneWidth,sceneHeight);
        double toX = (double) coordinate.get("toX");
        double toY = (double) coordinate.get("toY");

        // Initial Position
        node.setLayoutX(toX);
        node.setLayoutY(toY);

        helper.addNodeListeners(stage,node,position,width,height);
    }

    protected void positioningRoute(Node node,NotificationPos position,Boolean animation,double duration) {
        if (position == null) return;

        if (animation) {
            NotifierAnimations anim = new NotifierAnimations();
            anim.animate(node,position,stage,duration);

            return;
        }

        setInanimatePosition(node,position);
    }

    // Method for displaying any type of notification
    public void show(Node node,NotificationPos position,Boolean animation,double duration) {
        if (! notificationRoot.getChildren().contains(node))
            notificationRoot.getChildren().add(node);

        notificationRoot.setVisible(true);

        // Setting the position of the notification type
        positioningRoute(node,position,animation,duration);
    }

    public void close() {
        notificationRoot.getChildren().clear();
        notificationRoot.setVisible(false);

        (new Helper()).removeListeners(stage);
        (new NotifierAnimations()).removeListeners(stage);
    }
}
