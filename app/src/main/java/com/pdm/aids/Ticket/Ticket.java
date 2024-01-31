package com.pdm.aids.Ticket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Ticket {

    private String ticketUuid;
    private String bookingUuid;
    private int ticketStatusId;
    private String title;
    private String description;
    private ArrayList<TicketImage> ticketImages;
    public Ticket(String id, String bookingId, int ticketStatusId, String title, String description) {
        this.ticketUuid = id;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingUuid = bookingId;
    }
    public Ticket(String id, String bookingId, int ticketStatusId, String title, String description, ArrayList<TicketImage> ticketImages) {
        this.ticketUuid = id;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingUuid = bookingId;
        this.ticketImages = ticketImages;
    }

    public String toJsonWithoutImages() {
        Gson gson = new Gson();

        Ticket ticketWithoutImages = new Ticket(ticketUuid, bookingUuid, ticketStatusId, title, description);
        return gson.toJson(ticketWithoutImages);
    }

    public String toJsonImagesOnly() {
        Gson gson = new Gson();

        String imagesJson = gson.toJson(ticketImages);
        return imagesJson;
    }

    public String getId() {
        return ticketUuid;
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
        return bookingUuid;
    }

    public int getTicketStatusId() {
        return ticketStatusId;
    }

    public void setTicketStatusId(int ticketStatusId) {
        this.ticketStatusId = ticketStatusId;
    }

    public void setId(String id) {
        this.ticketUuid = id;
    }

    public void setBookingId(String bookingId) {
        this.bookingUuid = bookingId;
    }

    public void setTicketImages(ArrayList<TicketImage> ticketImages) {
        this.ticketImages = ticketImages;
    }
}
