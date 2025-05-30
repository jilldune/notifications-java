# 📢 notifications-java

A JavaFX-based notification library for displaying customizable toast messages, alerts,
and more — powered by [Ikonli](https://kordamp.org/ikonli/) and [ControlsFX](https://github.com/controlsfx/controlsfx).

## ✨ Features

- Lightweight notifications [Dialog, Toast, Prompt, Notification]
- JavaFX integration
- Icon support using Ikonli, Fluent
- and easy-to-use API

## 📦 Installation

Currently not published to Maven Central. To use this in your project:

1. **Build the shaded JAR:**

```bash
mvn clean package
```

Usage example
```java
import com.edubiz.notificationsjava.NotifyManager;
import com.edubiz.notificationsjava.notification.NotifyType;
import com.edubiz.notificationsjava.notification.Toast;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Initialize NotifyManager
        NotifyManager manager = new NotifyManager(stage);

        // Create a toast notification
        Toast toast = manager.create(NotifyType.TOAST);

        // Show the toast with a message
        toast.create("Hello from notifications-java!");
    }

    public static void main(String[] args) {
        launch();
    }
}
```

