package com.pdm.aids.Ticket;

import static android.content.Context.MODE_PRIVATE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pdm.aids.Login.LoginActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket {

    private String ticketUuid;
    private Date creationDate;
    private Date lastModified;
    private String bookingUuid;
    private int ticketStatusId;
    private String title;
    private String description;
    private int createdBy;
    private ArrayList<TicketImage> ticketImages;
    public Ticket() {
    }
    public Ticket(String uuid, String bookingId, int ticketStatusId, String title, String description) {
        this.ticketUuid = uuid;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingUuid = bookingId;
    }
    public Ticket(String uuid, String bookingId, int ticketStatusId, String title, String description, Date creationDate, Date lastModified) {
        this.ticketUuid = uuid;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingUuid = bookingId;
        this.creationDate = creationDate;
        this.lastModified = lastModified;
    }
    public Ticket(String id, String bookingId, int ticketStatusId, String title, String description, Date creationDate, Date lastModified, ArrayList<TicketImage> ticketImages) {
        this.ticketUuid = id;
        this.title = title;
        this.description = description;
        this.ticketStatusId = ticketStatusId;
        this.bookingUuid = bookingId;
        this.creationDate = creationDate;
        this.lastModified = lastModified;
        this.ticketImages = ticketImages;
    }

    public String toJsonWithoutImages(int id) {
        Gson gson = new Gson();

        System.out.println(id);
        Ticket ticketWithoutImages = new Ticket(ticketUuid, bookingUuid, ticketStatusId, title, description, creationDate, lastModified);
        ticketWithoutImages.setUserId(id);
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

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setUserId(int userId) {
        this.createdBy = userId;
    }
}
