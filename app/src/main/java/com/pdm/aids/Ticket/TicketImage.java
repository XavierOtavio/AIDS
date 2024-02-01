package com.pdm.aids.Ticket;

public class TicketImage {
    String id;
    String ticketUuid;
    String filename;
    String image;
    String imagePath;

    public TicketImage(String ticketId, String filename, String image){
        this.ticketUuid = ticketId;
        this.filename = filename;
        this.image = image;
    }
    public TicketImage() {}
    public String  getId() {
        return id;
    }

    public String getImage() {
        return image;
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

    public void setId(String  id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTicketUuid(String ticketId) {
        this.ticketUuid = ticketId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
