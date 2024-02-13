package com.pdm.aids.Room;

import java.util.Date;

public class Room {
    int id;
    String name, description;
    Date LastUpdate;

    public Room(int id, String name, String description, Date modifiedDate){
        this.id = id;
        this.name = name;
        this.description = description;
        this.LastUpdate = modifiedDate;
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Date getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.LastUpdate = lastUpdate;
    }
}
