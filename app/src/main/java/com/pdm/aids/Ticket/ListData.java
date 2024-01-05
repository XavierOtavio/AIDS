package com.pdm.aids.Ticket;

public class ListData {
    String title, description, id;
    public ListData(int id, String title, String description) {
        this.title = title;
        this.description = description;
        this.id = "ID" + id;
    }
}
