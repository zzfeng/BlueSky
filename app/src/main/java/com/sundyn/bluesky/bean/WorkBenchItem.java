package com.sundyn.bluesky.bean;

public class WorkBenchItem {
    private int imageID;
    private String name;
    private int modleID;

    public int getModleID() {
        return modleID;
    }

    public void setModleID(int modleID) {
        this.modleID = modleID;
    }

    public WorkBenchItem(int imageID, String name, int modleID) {
        super();
        this.imageID = imageID;
        this.name = name;
        this.modleID = modleID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
