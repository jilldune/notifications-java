package com.edubiz.notificationsjava.Notifications;

public enum ToastType {
    SUCCESS("success","s"),
    ERROR("error","e"),
    INFO("info","i"),
    ALERT("alert","a"),
    NEUTRAL("neutral","n");
    
    private final String typeName;
    private final String shorthand;
    
    ToastType(String typeName,String shorthand) {
        this.typeName = typeName;
        this.shorthand = shorthand;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public String getShorthand() {
        return shorthand;
    }
    
    public static ToastType fromString(String type) {
        for (ToastType toastType : ToastType.values()) {
            if (toastType.typeName.equalsIgnoreCase(type) || toastType.shorthand.equalsIgnoreCase(type))
                return toastType;
        }
        
        throw new IllegalArgumentException("Invalid toast type:"+type);
    }
}
