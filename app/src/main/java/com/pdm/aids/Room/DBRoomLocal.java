package com.pdm.aids.Room;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.pdm.aids.Booking.DBBookingLocal;

import java.util.ArrayList;
import java.util.List;

public class DBRoomLocal {
    public static final String ROOM_TABLE = "ROOM_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    public static String CreateTableRoom() {
        return "CREATE TABLE IF NOT EXISTS " + ROOM_TABLE + " (" +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT" +
                ");";
    }

    public static void addRoom(Room room, Context context, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, room.getId());
        values.put(COLUMN_NAME, room.getName());
        values.put(COLUMN_DESCRIPTION, room.getDescription());

        db.insert(ROOM_TABLE, null, values);
    }

    @SuppressLint("Range")
    public List<Room> getAllRooms(SQLiteDatabase db) {
        List<Room> roomList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ROOM_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

                Room room = new Room(id, name, description);
                roomList.add(room);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomList;
    }

    @SuppressLint("Range")
    public static Room getRoomById(int roomId, SQLiteDatabase db) {

        Cursor cursor = db.query(ROOM_TABLE,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(roomId)},
                null, null, null, null);

        Room room = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));

            room = new Room(id, name, description);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return room;
    }
}
