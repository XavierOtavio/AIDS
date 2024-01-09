package com.pdm.aids.Room;

public class Room {
    int id;
    String name, description;

    public Room(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
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
}
