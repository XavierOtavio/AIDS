package com.pdm.aids.Booking;

import java.util.Date;

public class Booking {
    private int id;
    private int roomId;
    private int userId;
    private int bookingStatusId;
    private Date expectedStartDate;
    private Date expectedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Date last_modified;
    private String hash;

    public Booking(int id, int roomId, int userId, int bookingStatusId, Date expectedStartDate,
                   Date expectedEndDate, Date actualStartDate, Date actualEndDate, Date last_modified, String hash) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.bookingStatusId = bookingStatusId;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.last_modified = last_modified;
        this.hash = hash;
    }
    public Booking(int roomId, int userId, int bookingStatusId, Date expectedStartDate,
                   Date expectedEndDate, Date actualStartDate, Date actualEndDate, Date last_modified, String hash) {
        this.roomId = roomId;
        this.userId = userId;
        this.bookingStatusId = bookingStatusId;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.last_modified = last_modified;
        this.hash = hash;
    }

    public Booking(int roomId, int userId, Date expectedStartDate, Date expectedEndDate) {
        this.roomId = roomId;
        this.userId = userId;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
    }

    public int getId() {
        return id;
    }

    public int getBookingStatusId() {
        return bookingStatusId;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public Date getExpectedEndDate() {
        return expectedEndDate;
    }

    public Date getExpectedStartDate() {
        return expectedStartDate;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getUserId() {
        return userId;
    }

    public String getHash() {
        return hash;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public void setBookingStatusId(int bookingStatusId) {
        this.bookingStatusId = bookingStatusId;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public void setExpectedEndDate(Date expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public void setExpectedStartDate(Date expectedStartDate) {
        this.expectedStartDate = expectedStartDate;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", userId=" + userId +
                ", bookingStatusId=" + bookingStatusId +
                ", expectedStartDate=" + expectedStartDate +
                ", expectedEndDate=" + expectedEndDate +
                ", actualStartDate=" + actualStartDate +
                ", actualEndDate=" + actualEndDate +
                ", last_modified=" + last_modified +
                ", hash='" + hash + '\'' +
                '}';
    }
}
