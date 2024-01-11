package com.pdm.aids.Booking;

import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.RoomImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    String roomName;
    String expectedDate;
    byte[] roomImage;
    public ListData(String roomName, Date startDate, Date endDate) {
        this.roomName = roomName;
        //TODO: get room image from database
        this.roomImage = null;
        this.expectedDate = new ExpectedDate(startDate, endDate).toString();
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
