package com.pdm.aids.Room;

public class RoomImage {
    private int imageId;
    private String fileName;
    private byte[] imageBytes;
    private int roomId;

    public RoomImage(int imageId, String fileName, byte[] imageBytes, int roomId) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.imageBytes = imageBytes;
        this.roomId = roomId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
