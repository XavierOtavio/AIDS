package com.pdm.aids.Ticket;

import java.util.ArrayList;

public class Ticket {

    private String id;
    private String bookingId;
    private int ticketStatusId;
    private String title;
    private String description;
    private ArrayList<TicketImage> ticketImages;
    public Ticket(String id, String bookingId, int ticketStatusId, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingId = bookingId;
    }
    public Ticket(String bookingId, int ticketStatusId, String title, String description, ArrayList<TicketImage> ticketImages) {
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingId = bookingId;
        this.ticketImages = ticketImages;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "id=" + id + ", " +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getId() {
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

    public String  getBookingId() {
        return bookingId;
    }

    public int getTicketStatusId() {
        return ticketStatusId;
    }

    public void setTicketStatusId(int ticketStatusId) {
        this.ticketStatusId = ticketStatusId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}
