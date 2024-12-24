module com.edubiz.notificationsjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.remixicon;
    requires annotations;
    requires java.desktop;

    exports com.edubiz.notificationsjava.Notify;
    opens com.edubiz.notificationsjava.Notify to javafx.fxml;

    // comment when building
    exports com.edubiz.notificationsjava.test;
    opens com.edubiz.notificationsjava.test to javafx.fxml;
    exports com.edubiz.notificationsjava.Managers;
    opens com.edubiz.notificationsjava.Managers to javafx.fxml;
    exports com.edubiz.notificationsjava.NotifierUtil;
    opens com.edubiz.notificationsjava.NotifierUtil to javafx.fxml;
}