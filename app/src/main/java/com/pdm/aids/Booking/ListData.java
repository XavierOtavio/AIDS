package com.pdm.aids.booking;

public class ListData {
    String id;
    int roomId;
    byte[] qrImage;
    public ListData(int id, int roomId, byte[] qrImage) {
        this.roomId = roomId;
        this.qrImage = qrImage;
        this.id = "ID " + id;
    }
}