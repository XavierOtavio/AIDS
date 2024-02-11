package com.pdm.aids.Ticket.TicketList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    private String title, description, uuid;
    private Date startDate;
    private boolean isSynchronized;

    public ListData(String title, String description, Date date, String uuid, boolean isSynchronized) {
        this.title = title;
        this.description = description;
        this.startDate = date;
        this.uuid = uuid;
        this.isSynchronized = isSynchronized;
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

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public String getFormattedStartDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(startDate);
    }
}


