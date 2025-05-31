module com.octalinc.notificationsjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.remixicon;
    requires java.desktop;

    exports com.octalinc.notificationsjava.Managers;
    opens com.octalinc.notificationsjava.Managers to javafx.fxml;
    exports com.octalinc.notificationsjava.NotifierUtil;
    opens com.octalinc.notificationsjava.NotifierUtil to javafx.fxml;
    exports com.octalinc.notificationsjava.Notify;
    opens com.octalinc.notificationsjava.Notify to javafx.fxml;
}