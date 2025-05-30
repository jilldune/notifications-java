package com.octalinc.notificationsjava.NotifierUtil;

public enum NotifyAlert {
    SUCCESS("success","s"),
    ERROR("error","e"),
    INFO("info","i"),
    ALERT("alert","a"),
    NEUTRAL("neutral","n");
    
    private final String typeName;
    private final String shorthand;
    
    NotifyAlert(String typeName, String shorthand) {
        this.typeName = typeName;
        this.shorthand = shorthand;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public String getShorthand() {
        return shorthand;
    }
    
    public static NotifyAlert fromString(String type) {
        for (NotifyAlert toastType : NotifyAlert.values()) {
            if (toastType.typeName.equalsIgnoreCase(type) || toastType.shorthand.equalsIgnoreCase(type))
                return toastType;
        }
        
        throw new IllegalArgumentException("Invalid toast type:"+type);
    }
}
