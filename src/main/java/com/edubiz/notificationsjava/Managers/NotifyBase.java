package com.edubiz.notificationsjava.Managers;

import com.edubiz.notificationsjava.NotifierUtil.NotifyUtils;
import com.edubiz.notificationsjava.NotifierUtil.NotifyPos;
import com.edubiz.notificationsjava.NotifierUtil.NotifyAnimation;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

import static com.edubiz.notificationsjava.NotifierUtil.NotifyUtils.timeOut;

public abstract class NotifyBase {
    protected Stage stage;
    private final Region layout;
    private Node node = null;
    private AnchorPane root;
    private NotifyAnimation animations;
    private Boolean animation = true;
    private Boolean animateNotification = true;
    private NotifyPos position = NotifyPos.TOP;
    private double duration = 5;
    private final Map<String, Object> closeCallbacks = new HashMap<>();
    private Runnable onCloseRequest;
    private Runnable onClosed;

    public NotifyBase(Stage stage, Region layout) {
        this.stage = stage;
        this.layout = layout;
    }

    public Region getLayout() { return this.layout; }

    // creates or applies the positions to the notification
    protected void setInanimatePosition(Node node, NotifyPos position) {
        this.animation = false;
        Scene scene = stage.getScene();
        NotifyUtils notifyUtils = new NotifyUtils();

        // get geometry
        Map<String, Double> helperGeometry = notifyUtils.getGeometry(node,scene);

        // Set bounds
        double width = helperGeometry.get("width");
        double height = helperGeometry.get("height");

        // Get the scene bounds
        double sceneWidth = helperGeometry.get("sceneWidth");
        double sceneHeight = helperGeometry.get("sceneHeight");

        // Define positioning
        Map<String, Object> coordinate = notifyUtils.parsePosition(position,width,height,sceneWidth,sceneHeight);
        double toX = (double) coordinate.get("toX");
        double toY = (double) coordinate.get("toY");

        // Initial Position
        node.setLayoutX(toX);
        node.setLayoutY(toY);

        root.setVisible(true);
        root.setManaged(true);

        notifyUtils.addNodeListeners(stage,node,position,width,height);
    }

    protected void positioningRoute(Node node, NotifyPos position, Boolean animation, double duration) {
        if (position == null) return;

        if (animation) {
            this.animation = true;
            this.animations = new NotifyAnimation();
            this.animations.animate(node,position,this.stage,duration);

            this.root.setVisible(true);
            this.root.setManaged(true);

            return;
        }

        setInanimatePosition(node,position);
    }

    // A function fired right before the close of the notification
    public void setOnCloseRequest(Runnable callback) {
        if (callback != null)
            this.onCloseRequest = callback;
    }

    // A function fired after the notification is closed
    public void setOnClosed(Runnable callback) {
        if (callback != null)
            this.onClosed = callback;
    }

    private void callOnCloseRequest() {
        if (this.onCloseRequest != null)
            this.onCloseRequest.run();
    }
    private void callOnClosed() {
        if (this.onClosed != null)
            this.onClosed.run();
    }

    // Method for displaying any type of notification
    protected void show(Node node, NotifyPos position, Boolean animation, double duration) {
        setProps(node,position,animation,duration);
    }

    private void setProps(Node node, NotifyPos pos, Boolean animation, double duration) {
        this.node = node;
        this.position = pos;
        this.animateNotification = animation;
        this.duration = duration;
    }

    protected void close() {
        Platform.runLater(this::run);
    }

    public void display(Runnable callback) {
        // Setting the position of the notification type
        positioningRoute(this.node,this.position,this.animateNotification,this.duration);

        if (callback != null)
            closeCallbacks.put("notify::close",callback);
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }

    private void run() {
        if (this.node != null) {
            if (this.animation != null) {
                this.callOnCloseRequest();
                this.animations.reverseTransition(() -> {
                    this.callOnClosed();
                    this.root.setManaged(false);
                    this.root.setVisible(false);

                    if (!this.closeCallbacks.isEmpty()) {
                        timeOut(() -> {
                            Runnable callback = (Runnable) closeCallbacks.get("notify::close");
                            callback.run();
                        }, .1);
                    }
                    (new NotifyUtils()).removeListeners(stage);
                    (new NotifyAnimation()).removeListeners(stage);
                });

                return;
            }

            this.callOnCloseRequest();
            this.root.setManaged(false);
            this.root.setVisible(false);
            // checks if callback was provided to close function
            if (!this.closeCallbacks.isEmpty()) {
                timeOut(() -> {
                    Runnable callback = (Runnable) closeCallbacks.get("notify::close");
                    callback.run();
                }, .1);
            }
            (new NotifyUtils()).removeListeners(stage);
            (new NotifyAnimation()).removeListeners(stage);
            this.callOnClosed();
        }
    }
}
