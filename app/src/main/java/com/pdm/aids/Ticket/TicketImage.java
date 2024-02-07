package com.pdm.aids.Ticket;

public class TicketImage {
    String id;
    String ticketUuid;
    String filename;
    String imagePath;
    String image;

    public TicketImage(String ticketId, String filename, String imagePath, String image) {
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.imagePath = imagePath;
        this.image = image;
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
