package com.pdm.aids.Ticket;

import java.util.Date;

public class TicketImage {
    String id;
    String ticketUuid;
    String filename;
    String imagePath;
    Boolean isUploaded;
    Date last_modified;
    String image;

    public TicketImage(String ticketId, String filename, String imagePath, String image, Date last_modified, Boolean isUploaded) {
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.imagePath = imagePath;
        this.image = image;
        this.last_modified = last_modified;
        this.isUploaded = isUploaded;
    }

    public TicketImage(String ticketId, String filename, String imagePath, Date last_modified, Boolean isUploaded) {
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.imagePath = imagePath;
        this.last_modified = last_modified;
        this.isUploaded = isUploaded;
    }

    public TicketImage(String ticketId, String filename, String image, Date last_modified) {
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.image = image;
        this.last_modified = last_modified;
    }

    public TicketImage(String ticketId, String filename, String imagePath) {
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.imagePath = imagePath;
    }

    public TicketImage() {
    }

    public String getId() {
        return id;
    }

    public String getTicketUuid() {
        return ticketUuid;
    }

    public String getFilename() {
        return filename;
    }

    public Boolean getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(Boolean isUploaded) {
        this.isUploaded = isUploaded;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTicketUuid(String ticketId) {
        this.ticketUuid = ticketId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
