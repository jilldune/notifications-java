package com.edubiz.notificationsjava.Notify;

import java.util.HashMap;
import java.util.Map;

public class Coordinates {
    public double fromX = 0.0;
    public double fromY = 0.0;
    public double toX = 0.0;
    public double toY = 0.0;
    public double pad = 0.0;
    public String position = "center";

    private final Map<String, Object> coordinates = new HashMap<>();

    public Coordinates() {}
    public Coordinates(double fromX, double fromY, double toX, double toY, double pad, String position) {
        setCoordinates(fromX,fromY,toX,toY,pad,position);
    }

    public void setCoordinates(double fromX, double fromY, double toX, double toY, double pad,String position) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.pad = pad;
        this.position = position;

        coordinates.put("fromX", this.fromX);
        coordinates.put("fromY", this.fromY);
        coordinates.put("toX", this.toX);
        coordinates.put("toY", this.toY);
        coordinates.put("pad", this.pad);
        coordinates.put("position",this.position);
    }

    public Map<String, Object> getCoordinates() {
        return this.coordinates;
    }
}
