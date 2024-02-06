package com.pdm.aids.Ticket;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Base64;

public class DBTicketLocal {

    // Ticket table
    public static final String TICKET_TABLE = "TICKET_TABLE";
    public static final String COLUMN_TICKET_ID = "TICKET_ID";
    public static final String COLUMN_BOOKING_ID = "BOOKING_ID";
    public static final String COLUMN_TICKET_STATUS_ID = "TICKET_STATUS_ID";
    public static final String COLUMN_TICKET_STARTDATE = "TICKET_STARTDATE";
    public static final String COLUMN_TICKET_MODIFIED = "TICKET_MODIFIED";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    // TicketImage table
    public static final String TICKET_IMAGE_TABLE = "TICKET_IMAGE_TABLE";
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_TICKET_IMAGE_ID = "TICKET_ID";
    public static final String COLUMN_FILENAME = "FILENAME";
    public static final String COLUMN_IMAGE_PATH = "IMAGE_PATH";
    public static final String COLUMN_IMAGE = "IMAGE";

    public static String CreateTicketTable() {
        return "CREATE TABLE " + TICKET_TABLE + " (" +
                COLUMN_TICKET_ID + " TEXT, " +
                COLUMN_BOOKING_ID + " TEXT, " +
                COLUMN_TICKET_STATUS_ID + " INTEGER, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT" +
                ")";
    }

    public static String CreateTicketImageTable(){
        return "CREATE TABLE " + TICKET_IMAGE_TABLE + " (" +
                   COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   COLUMN_TICKET_IMAGE_ID + " INTEGER, " +
                   COLUMN_FILENAME + " TEXT, " +
                   COLUMN_IMAGE + " BLOB, " +
                   "FOREIGN KEY(" + COLUMN_TICKET_IMAGE_ID + ") REFERENCES " +
                   TICKET_TABLE + "(" + COLUMN_TICKET_ID + ")" +
                   ")";
       }
    public static boolean createTicket(Ticket ticket, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKET_ID, ticket.getId());
        cv.put(COLUMN_BOOKING_ID, ticket.getBookingId());
        cv.put(COLUMN_TICKET_STATUS_ID, ticket.getTicketStatusId());
        cv.put(COLUMN_TITLE, ticket.getTitle());
        cv.put(COLUMN_DESCRIPTION, ticket.getDescription());

        long insert = db.insert(TICKET_TABLE, null, cv);
        return insert != -1;
    }

    public static boolean createTicketImage(TicketImage ticketImage, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKET_IMAGE_ID, ticketImage.getTicketUuid());
        cv.put(COLUMN_FILENAME, ticketImage.getFilename());
        cv.put(COLUMN_IMAGE, ticketImage.getImage());

        long insert = db.insert(TICKET_IMAGE_TABLE, null, cv);
        return insert != -1;
    }

    @SuppressLint("Range")
    public ArrayList<Ticket> getAllTicketsWithImages(SQLiteDatabase db) {
        ArrayList<Ticket> ticketList = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String ticketId = cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_ID));
                String bookingId = cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_ID));
                int ticketStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_TICKET_STATUS_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                ArrayList<TicketImage> ticketImages = getTicketImagesForTicket(ticketId, db);

                Ticket ticket = new Ticket(ticketId,bookingId, ticketStatusId, title, description, ticketImages);
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketList;
    }

    @SuppressLint("Range")
    private ArrayList<TicketImage> getTicketImagesForTicket(String ticketId, SQLiteDatabase db) {
        ArrayList<TicketImage> ticketImages = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE +
                " WHERE " + COLUMN_TICKET_IMAGE_ID + " = '" + ticketId + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                String filename = cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME));
                byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                String base64Image = Base64.getEncoder().encodeToString(image);


                TicketImage ticketImage = new TicketImage(ticketId, filename, base64Image);
                ticketImages.add(ticketImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketImages;
    }

    @SuppressLint("Range")
    public ArrayList<byte[]> getByteTicketImagesForTicket(String ticketId, SQLiteDatabase db) {
        ArrayList<byte[]> ticketImages = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE +
                " WHERE " + COLUMN_TICKET_IMAGE_ID + " = '" + ticketId + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                byte[] image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                ticketImages.add(image);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketImages;
    }


    @SuppressLint("Range")
    public ArrayList<Ticket> getAllTicketsByBookingId(String bookingId, SQLiteDatabase db) {
        ArrayList<Ticket> ticketList = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_TABLE +
                " WHERE " + COLUMN_BOOKING_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{bookingId});

        if (cursor.moveToFirst()) {
            do {
                String ticketId = cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_ID));
                int statusId = cursor.getInt(cursor.getColumnIndex(COLUMN_TICKET_STATUS_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                ArrayList<TicketImage> ticketImages = getTicketImagesForTicket(ticketId, db);

                Ticket ticket = new Ticket(ticketId, bookingId, statusId, title, description, ticketImages);
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketList;
    }



}
