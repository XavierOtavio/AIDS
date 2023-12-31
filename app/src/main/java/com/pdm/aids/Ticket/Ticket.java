package com.pdm.aids.Ticket;

public class Ticket {

    private int id;
    private String title;
    private String description;
    private byte[] picture;

    public Ticket(int id, String title, String description, byte[] picture) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picture = picture;
    }

    public Ticket(String title, String description, byte[] picture) {
        this.title = title;
        this.description = description;
        this.picture = picture;
    }

    public Ticket(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id=" + id + ", " +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // Getters and Setters for existing fields
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
