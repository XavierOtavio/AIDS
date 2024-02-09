package com.pdm.aids.Ticket.TicketList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    private String title, description, uuid;
    private Date startDate;

    public ListData(String title, String description, Date date, String uuid) {
        this.title = title;
        this.description = description;
        this.startDate = date;
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getFormattedStartDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(startDate);
    }
}


