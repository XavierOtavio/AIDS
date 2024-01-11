package com.pdm.aids.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static Date convertUnixToDate(String unix) {
        try {
            Date date = new Date(Long.parseLong(unix) * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateFormat.format(date));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date string", e);
        }
    }


    public static boolean isDateNull(Date dateToCheck) {
        try {
            Date date = new Date(Long.parseLong("-2208988800") * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateToCheck.equals(dateFormat.parse(dateFormat.format(date)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
