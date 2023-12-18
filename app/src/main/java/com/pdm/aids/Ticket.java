package com.pdm.aids;

public class Ticket {

    private int id;
    private String title;
    private String description;

    public Ticket(int id, String title, String description) {
        this.id = id;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
