package com.edubiz.notificationsjava.NotifierUtil;

import java.util.HashMap;
import java.util.Map;

public class NotifyPropsWrapper {
    private final Map<String, Object> properties = new HashMap<>();

    public <T> void put(String key, T value) {
        properties.put(key, value);
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(properties.get(key));
    }
}

