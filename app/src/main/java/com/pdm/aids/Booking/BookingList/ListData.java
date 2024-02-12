package com.pdm.aids.Booking.BookingList;

import android.graphics.Bitmap;

import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.RoomImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    String roomName;
    String expectedStartDate;
    String expectedEndDate;
    Integer bookingStatusId;
    SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH:mm", Locale.getDefault());
    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public ListData(String roomName, Date startDate, Date endDate, Integer bookingStatusId) {
        this.roomName = roomName;
        this.bookingStatusId = bookingStatusId;
        this.expectedStartDate = dateFormatHour.format(startDate) + "\n" + dateFormatDay.format(startDate);
        this.expectedEndDate = dateFormatHour.format(endDate) + "\n" + dateFormatDay.format(endDate);
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
//        @Override
//        public String toString() {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//            String startDateStr = dateFormat.format(startDate);
//            String endDateStr = dateFormat.format(endDate);
//            return startDateStr + "\n" + endDateStr;
//        }
    }

}
