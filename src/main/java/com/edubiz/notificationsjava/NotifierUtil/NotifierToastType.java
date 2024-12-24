package com.edubiz.notificationsjava.NotifierUtil;

public enum NotifierToastType {
    SUCCESS("success","s"),
    ERROR("error","e"),
    INFO("info","i"),
    ALERT("alert","a"),
    NEUTRAL("neutral","n");
    
    private final String typeName;
    private final String shorthand;
    
    NotifierToastType(String typeName, String shorthand) {
        this.typeName = typeName;
        this.shorthand = shorthand;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public String getShorthand() {
        return shorthand;
    }
    
    public static NotifierToastType fromString(String type) {
        for (NotifierToastType toastType : NotifierToastType.values()) {
            if (toastType.typeName.equalsIgnoreCase(type) || toastType.shorthand.equalsIgnoreCase(type))
                return toastType;
        }
        
        throw new IllegalArgumentException("Invalid toast type:"+type);
    }
}
