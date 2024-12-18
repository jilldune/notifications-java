module com.edubiz.notificationsjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires annotations;
    requires java.desktop;
    requires org.kordamp.ikonli.remixicon;
    requires org.kordamp.ikonli.core;

    opens com.edubiz.notificationsjava to javafx.fxml;
    exports com.edubiz.notificationsjava;
    exports com.edubiz.notificationsjava.Notifications;
    opens com.edubiz.notificationsjava.Notifications to javafx.fxml;
}