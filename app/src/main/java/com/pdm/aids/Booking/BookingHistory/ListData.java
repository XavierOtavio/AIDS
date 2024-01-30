package com.pdm.aids.Booking.BookingHistory;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    String roomName;
    String expectedDate;
    Bitmap roomImage;

    public ListData(String roomName, Date startDate, Date endDate, Bitmap roomImage) {
        this.roomName = roomName;
        this.roomImage = roomImage;
        this.expectedDate = new com.pdm.aids.Booking.BookingHistory.ListData.ExpectedDate(startDate, endDate).toString();
    }

    public class ExpectedDate {
        private Date startDate;
        private Date endDate;

        public ExpectedDate(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);
            return startDateStr + "\n" + endDateStr;
        }
    }

}