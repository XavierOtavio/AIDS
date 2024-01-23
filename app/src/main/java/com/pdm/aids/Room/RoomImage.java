package com.pdm.aids.Room;

public class RoomImage {
    private int imageId;
    private String fileName;
    private String imagePath;
    private int roomId;

    public RoomImage(int imageId, String fileName, String imagePath, int roomId) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.imagePath = imagePath;
        this.roomId = roomId;
    }

    public RoomImage() {

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
