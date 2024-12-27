package com.edubiz.notificationsjava.Managers;

import com.edubiz.notificationsjava.NotifierUtil.Helper;
import com.edubiz.notificationsjava.NotifierUtil.NotificationPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifierAnimations;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Map;

public abstract class BaseNotifier {
    protected Stage stage;
    private final Region layout;
    private Node node = null;
    private AnchorPane root;
    private NotifierAnimations animations;
    private Boolean animation = true;

    public BaseNotifier(Stage stage, Region layout) {
        this.stage = stage;
        this.layout = layout;
    }

    public Region getLayout() { return this.layout; }

    // creates or applies the positions to the notification
    protected void setInanimatePosition(Node node, NotificationPos position) {
        this.animation = false;
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

        root.setVisible(true);
        root.setManaged(true);

        helper.addNodeListeners(stage,node,position,width,height);
    }

    protected void positioningRoute(Node node,NotificationPos position,Boolean animation,double duration) {
        if (position == null) return;

        if (animation) {
            this.animation = true;
            animations = new NotifierAnimations();
            animations.animate(node,position,stage,duration);

            root.setVisible(true);
            root.setManaged(true);
            return;
        }

        setInanimatePosition(node,position);
    }

    // Method for displaying any type of notification
    protected void show(Node node,NotificationPos position,Boolean animation,double duration) {
        Platform.runLater(()->{
            this.node = node;

            // Setting the position of the notification type
            positioningRoute(node,position,animation,duration);
        });
    }

    protected void close() {
        if (this.node != null) {
            if (this.animation) {
                animations.reverseTransition(() -> {
                    Pane root = (Pane) this.node;
                    root.getParent().setVisible(false);
                    root.setManaged(false);

                    (new Helper()).removeListeners(stage);
                    (new NotifierAnimations()).removeListeners(stage);
                });

                return;
            }


            Pane root = (Pane) this.node;
            root.getParent().setVisible(false);
            root.setManaged(false);

            (new Helper()).removeListeners(stage);
            (new NotifierAnimations()).removeListeners(stage);
        }
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }
}
