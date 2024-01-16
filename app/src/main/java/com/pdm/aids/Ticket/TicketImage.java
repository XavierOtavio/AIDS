package com.pdm.aids.Ticket;

public class TicketImage {
    String id;
    String ticketId;
    String filename;
    byte[] image;

    public TicketImage(String ticketId, String filename, byte[] image){
        this.ticketId = ticketId;
        this.filename = filename;
        this.image = image;

    }

    public String  getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public String getTicketId() {
        return ticketId;
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

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
