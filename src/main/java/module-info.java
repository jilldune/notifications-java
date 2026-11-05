module com.octalinc.notificationsjava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.remixicon;
    requires java.desktop;

    exports com.octalinc.notificationsjava.core;
    opens com.octalinc.notificationsjava.core to javafx.fxml;
    exports com.octalinc.notificationsjava.utils;
    opens com.octalinc.notificationsjava.utils to javafx.fxml;
    exports com.octalinc.notificationsjava.components;
    opens com.octalinc.notificationsjava.components to javafx.fxml;
    exports com.octalinc.notificationsjava.enums;
    opens com.octalinc.notificationsjava.enums to javafx.fxml;
}