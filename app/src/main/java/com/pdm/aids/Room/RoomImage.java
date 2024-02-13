package com.pdm.aids.Room;

import android.graphics.Bitmap;

import java.util.Date;

public class RoomImage {
    private int imageId;
    private String fileName;
    private String imagePath;
    private Bitmap imageBitmap;
    private int roomId;
    private Date last_modified;

    public RoomImage(int imageId, String fileName, String imagePath, int roomIdm, Date last_modified) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.imagePath = imagePath;
        this.roomId = roomId;
        this.last_modified = last_modified;
    }

    public RoomImage(int imageId, String fileName, Bitmap imageBitmap, int roomId) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.imageBitmap = imageBitmap;
        this.roomId = roomId;
    }

    public RoomImage() {

    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
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
