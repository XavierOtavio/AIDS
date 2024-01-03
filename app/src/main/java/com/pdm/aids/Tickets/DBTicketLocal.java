package com.pdm.aids.Tickets;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBTicketLocal extends SQLiteOpenHelper {

    public static final String TICKET_TABLE = "TICKET_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_PICTURE = "PICTURE";

    public DBTicketLocal(@Nullable Context context) {
        super(context, "ticket.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TICKET_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PICTURE + " BLOB)";
        db.execSQL(createTableStatement);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean createTicket(Ticket ticket){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, ticket.getTitle());
        cv.put(COLUMN_DESCRIPTION, ticket.getDescription());
        cv.put(COLUMN_PICTURE, ticket.getPicture());

        long insert= db.insert(TICKET_TABLE, null, cv);
        return insert != -1;
    }

    public boolean updateTicket(Ticket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, ticket.getTitle());
        cv.put(COLUMN_DESCRIPTION, ticket.getDescription());
        cv.put(COLUMN_PICTURE, ticket.getPicture());

        long update = db.update(TICKET_TABLE, cv, COLUMN_ID + " = ?",
                new String[]{String.valueOf(ticket.getId())});
        return update != -1;
    }

    public boolean deleteTicket(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long delete = db.delete(TICKET_TABLE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        return delete != -1;
    }

    public ArrayList<Ticket> getAllTickets(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Ticket> ticketList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TICKET_TABLE, null);

        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") byte[] picture = cursor.getBlob(cursor.getColumnIndex(COLUMN_PICTURE));

                Ticket ticket = new Ticket(id, title, description, picture);
                ticketList.add(ticket);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ticketList;
    }

    public Ticket findTicketById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " + TICKET_TABLE + " WHERE " + COLUMN_ID + " = " + id;
        Cursor cursor = db.rawQuery(queryString, null);
        Ticket ticket = null;

        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            @SuppressLint("Range") byte[] picture = cursor.getBlob(cursor.getColumnIndex(COLUMN_PICTURE));

            ticket = new Ticket(id, title, description, picture);
        }

        cursor.close();
        return ticket;
    }
}
