package com.pdm.aids.booking;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;

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
}
