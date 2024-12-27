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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;

public abstract class BaseNotifier {
    protected Stage stage;
    private final Region layout;
    private Node node = null;
    private AnchorPane root;

    public BaseNotifier(Stage stage, Region layout) {
        this.stage = stage;
        this.layout = layout;
    }

    public Region getLayout() { return this.layout; }

    // creates or applies the positions to the notification
    protected void setInanimatePosition(Node node, NotificationPos position) {
        System.out.println("no animation...");
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
            NotifierAnimations anim = new NotifierAnimations();
            anim.animate(node,position,stage,duration);

            root.setVisible(true);
            root.setManaged(true);
            System.out.println("animating...");
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

            System.out.println(position);
        });
    }

    protected void close() {
        if (this.node != null) {
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

    public void clear() { close(); }
}
