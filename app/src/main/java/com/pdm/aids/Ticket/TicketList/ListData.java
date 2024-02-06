package com.pdm.aids.Ticket.TicketList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListData {
    private String title, description, uuid;
    private Date startDate;

    public ListData(String title, String description, Date startDate, String uuid) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.uuid = uuid;

        System.out.println("ListData created - Title: " + title + ", Description: " + description + ", UUID: " + uuid);
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

    public String getFormattedStartDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(startDate);
    }
}


