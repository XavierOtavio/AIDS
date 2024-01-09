package com.pdm.aids.Booking;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
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
    private Date dateTime;
    private String hash;
    private byte[] qrImage;

    public Booking(int roomId, int userId, int bookingStatusId, Date expectedStartDate,
                   Date expectedEndDate, Date actualStartDate, Date actualEndDate, Date dateTime, String hash) {
        this.roomId = roomId;
        this.userId = userId;
        this.bookingStatusId = bookingStatusId;
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.dateTime = dateTime;
        this.hash = hash;
    }



    public Booking(int roomId, int userId) {
        this.roomId = roomId;
        this.userId = userId;
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

    public Date getDateTime() {
        return dateTime;
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

    public byte[] getQrImage(String hash){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(hash,
                    BarcodeFormat.QR_CODE,
                    300,
                    300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
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

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
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

    public void setQrImage(byte[] qrImage) {
        this.qrImage = qrImage;
    }
}
