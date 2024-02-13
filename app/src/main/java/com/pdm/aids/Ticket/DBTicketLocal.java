package com.pdm.aids.Ticket;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.pdm.aids.Common.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class DBTicketLocal {

    // Ticket table
    public static final String TICKET_TABLE = "TICKET_TABLE";
    public static final String COLUMN_TICKET_ID = "TICKET_ID";
    public static final String COLUMN_BOOKING_ID = "BOOKING_ID";
    public static final String COLUMN_IS_SYNCHRONIZED = "IS_SYNCHRONIZED";
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
    public static final String COLUMN_IS_SYNCHRONIZED_IMAGE = "IS_SYNCHRONIZED";
    public static final String COLUMN_IMAGE_MODIFIED = "LAST_MODIFIED";

    public static String CreateTicketTable() {
        return "CREATE TABLE " + TICKET_TABLE + " (" +
                COLUMN_TICKET_ID + " TEXT, " +
                COLUMN_BOOKING_ID + " TEXT, " +
                COLUMN_IS_SYNCHRONIZED + " BOOLEAN, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_TICKET_STARTDATE + " DATE, " +
                COLUMN_TICKET_MODIFIED + " DATE, " +
                COLUMN_DESCRIPTION + " TEXT" +
                ")";
    }

    public static String CreateTicketImageTable() {
        return "CREATE TABLE " + TICKET_IMAGE_TABLE + " (" +
                COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TICKET_IMAGE_ID + " INTEGER, " +
                COLUMN_FILENAME + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_IS_SYNCHRONIZED_IMAGE + " BOOLEAN, " +
                COLUMN_IMAGE_MODIFIED + " DATE, " +
                "FOREIGN KEY(" + COLUMN_TICKET_IMAGE_ID + ") REFERENCES " +
                TICKET_TABLE + "(" + COLUMN_TICKET_ID + ")" +
                ")";
    }

    public static boolean createTicket(Ticket ticket, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKET_ID, ticket.getId());
        cv.put(COLUMN_BOOKING_ID, ticket.getBookingId());
        cv.put(COLUMN_IS_SYNCHRONIZED, ticket.getIsSynchronized());
        cv.put(COLUMN_TITLE, ticket.getTitle());
        cv.put(COLUMN_DESCRIPTION, ticket.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cv.put(COLUMN_TICKET_STARTDATE, dateFormat.format(ticket.getCreationDate()));
        cv.put(COLUMN_TICKET_MODIFIED, dateFormat.format(ticket.getLastModified()));

        long insert = db.insert(TICKET_TABLE, null, cv);
        return insert != -1;
    }

    public static boolean updateTicket(Ticket ticket, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKET_ID, ticket.getId());
        cv.put(COLUMN_BOOKING_ID, ticket.getBookingId());
        cv.put(COLUMN_IS_SYNCHRONIZED, ticket.getIsSynchronized());
        cv.put(COLUMN_TITLE, ticket.getTitle());
        cv.put(COLUMN_DESCRIPTION, ticket.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cv.put(COLUMN_TICKET_STARTDATE, dateFormat.format(ticket.getCreationDate()));
        cv.put(COLUMN_TICKET_MODIFIED, dateFormat.format(ticket.getLastModified()));

        long insert = db.update(TICKET_TABLE, cv, COLUMN_TICKET_ID + " = ?", new String[]{ticket.getId()});
        return insert != -1;
    }

    public static boolean createOrUpdateTicket(Ticket ticket, Context context, SQLiteDatabase db) throws ParseException {
        Ticket foundTicket = getTicketByUUID(ticket.getId(), db);
        if (foundTicket == null) {
            return createTicket(ticket, context, db);
        } else {
            if (foundTicket.getLastModified().before(ticket.getLastModified())) {
                return updateTicket(ticket, context, db);
            }
            return true;
        }
    }

    public static boolean createTicketImage(TicketImage ticketImage, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        cv.put(COLUMN_TICKET_IMAGE_ID, ticketImage.getTicketUuid());
        cv.put(COLUMN_FILENAME, ticketImage.getFilename());
        cv.put(COLUMN_IMAGE_PATH, ticketImage.getImagePath());
        cv.put(COLUMN_IS_SYNCHRONIZED_IMAGE, ticketImage.getIsUploaded());
        cv.put(COLUMN_IMAGE_MODIFIED, dateFormat.format(ticketImage.getLast_modified()));

        long insert = db.insert(TICKET_IMAGE_TABLE, null, cv);
        return insert != -1;
    }

    public static boolean updateTicketImage(TicketImage ticketImage, Context context, SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        cv.put(COLUMN_TICKET_IMAGE_ID, ticketImage.getTicketUuid());
        cv.put(COLUMN_FILENAME, ticketImage.getFilename());
        cv.put(COLUMN_IMAGE_PATH, ticketImage.getImagePath());
        cv.put(COLUMN_IS_SYNCHRONIZED_IMAGE, ticketImage.getIsUploaded());
        cv.put(COLUMN_IMAGE_MODIFIED, dateFormat.format(ticketImage.getLast_modified()));

        long insert = db.update(TICKET_IMAGE_TABLE, cv, COLUMN_FILENAME + " = ?", new String[]{ticketImage.getFilename()});
        return insert != -1;
    }

    public static boolean createOrUpdateTicketImage(TicketImage ticketImage, Context context, SQLiteDatabase db) {
        TicketImage foundTicketImage = getTicketImageByFilename(ticketImage, db);
        if (foundTicketImage == null) {
            return createTicketImage(ticketImage, context, db);
        } else {
            if (foundTicketImage.getLast_modified().before(ticketImage.getLast_modified())) {
                return updateTicketImage(ticketImage, context, db);
            }
            return true;
        }
    }


    @SuppressLint("Range")
    public static ArrayList<Ticket> getAllTicketsWithImages(SQLiteDatabase db) throws ParseException {
        ArrayList<Ticket> ticketList = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


                String ticketId = cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_ID));
                String bookingId = cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_ID));
                boolean isSynchronized = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SYNCHRONIZED)) > 0;
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                Date startDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_STARTDATE)));
                Date modifiedDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_MODIFIED)));

                ArrayList<TicketImage> ticketImages = getTicketImagesForTicket(ticketId, db);

                Ticket ticket = new Ticket(ticketId, bookingId, isSynchronized, title, description, startDate, modifiedDate, ticketImages);
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketList;
    }

    @SuppressLint("Range")
    private static ArrayList<TicketImage> getTicketImagesForTicket(String ticketId, SQLiteDatabase db) {
        ArrayList<TicketImage> ticketImages = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE +
                " WHERE " + COLUMN_TICKET_IMAGE_ID + " = '" + ticketId + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                String filename = cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME));
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                TicketImage ticketImage = new TicketImage(ticketId, filename, imagePath);
                ticketImages.add(ticketImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketImages;
    }

    @SuppressLint("Range")
    public ArrayList<String> getImagePath(String ticketId, SQLiteDatabase db) {
        ArrayList<String> ticketImages = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE +
                " WHERE " + COLUMN_TICKET_IMAGE_ID + " = '" + ticketId + "'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                ticketImages.add(image);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketImages;
    }


    @SuppressLint("Range")
    public static ArrayList<Ticket> getAllTicketsByBookingId(String bookingHash, SQLiteDatabase db) throws ParseException {
        ArrayList<Ticket> ticketList = new ArrayList<>();

        String query = "SELECT * FROM " + TICKET_TABLE +
                " WHERE " + COLUMN_BOOKING_ID + " = \"" + bookingHash + "\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                String ticketId = cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_ID));
                boolean isSynchronized = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SYNCHRONIZED)) > 0;
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                Date startDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_STARTDATE)));
                Date modifiedDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_MODIFIED)));


                ArrayList<TicketImage> ticketImages = getTicketImagesForTicket(ticketId, db);

                Ticket ticket = new Ticket(ticketId, bookingHash, isSynchronized, title, description, startDate, modifiedDate, ticketImages);
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ticketList;
    }

    @SuppressLint("Range")
    public static Ticket getTicketByUUID(String UUID, SQLiteDatabase db) throws ParseException {
        String query = "SELECT * FROM " + TICKET_TABLE +
                " WHERE " + COLUMN_TICKET_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{UUID});

        if (cursor.moveToFirst()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String bookingId = cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_ID));
            boolean isSynchronized = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SYNCHRONIZED)) > 0;
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            Date startDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_STARTDATE)));
            Date modifiedDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_MODIFIED)));

            ArrayList<TicketImage> ticketImages = getTicketImagesForTicket(UUID, db);

            return new Ticket(UUID, bookingId, isSynchronized, title, description, startDate, modifiedDate, ticketImages);
        }
        cursor.close();
        return null;
    }

    @SuppressLint("Range")
    public static ArrayList<Bitmap> getTicketImageByTicketId(String ticketId, SQLiteDatabase db) {
        ArrayList<Bitmap> imageBytes = new ArrayList<>();
        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE + " WHERE " + COLUMN_TICKET_IMAGE_ID + " = " + ticketId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    imageBytes.add(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                }
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return imageBytes;
    }

    @SuppressLint("Range")
    public static Bitmap getTicketImageByFilename(String filename, SQLiteDatabase db) {
        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE + " WHERE " + COLUMN_FILENAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{filename});
        if (cursor.moveToFirst()) {
            String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            }
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

    @SuppressLint("Range")
    public static TicketImage getTicketImageByFilename(TicketImage ticketImage, SQLiteDatabase db) {
        String query = "SELECT * FROM " + TICKET_IMAGE_TABLE + " WHERE " + COLUMN_FILENAME + " = \"" + ticketImage.getFilename() + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            String ticketId = cursor.getString(cursor.getColumnIndex(COLUMN_TICKET_IMAGE_ID));
            String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
            String filename = cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME));
            String image = Utils.imageConvert(BitmapFactory.decodeFile(new File(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH))).getAbsolutePath()));
            boolean isUploaded = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_SYNCHRONIZED_IMAGE)) > 0;
            Date last_modified = Utils.convertStringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_MODIFIED)));
            return new TicketImage(ticketId, filename, imagePath, image, last_modified, isUploaded);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return null;
    }

}
