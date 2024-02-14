package com.pdm.aids.Room;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.pdm.aids.Booking.DBBookingLocal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBRoomLocal {
    public static final String ROOM_TABLE = "ROOM_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_LAST_UPDATE = "LAST_UPDATE";

    public static String CreateTableRoom() {
        return "CREATE TABLE IF NOT EXISTS " + ROOM_TABLE + " (" +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LAST_UPDATE + " DATE" +
                ");";
    }

    public static void createRoom(Room room, Context context, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, room.getId());
        values.put(COLUMN_NAME, room.getName());
        values.put(COLUMN_DESCRIPTION, room.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_LAST_UPDATE, dateFormat.format(room.getLastUpdate()));

        db.insert(ROOM_TABLE, null, values);
    }

    public static void updateRoom(Room room, Context context, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, room.getId());
        values.put(COLUMN_NAME, room.getName());
        values.put(COLUMN_DESCRIPTION, room.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_LAST_UPDATE, dateFormat.format(room.getLastUpdate()));

        db.update(ROOM_TABLE, values, COLUMN_ID + "=?", new String[]{String.valueOf(room.getId())});
    }

    public static void deleteRoom(int roomId, Context context, SQLiteDatabase db) {
        db.delete(ROOM_TABLE, COLUMN_ID + "=?", new String[]{String.valueOf(roomId)});
    }

    public static void createOrUpdateRoom(Room room, Context context, SQLiteDatabase db) {
        Room roomInDb = getRoomById(room.getId(), db);
        if (roomInDb == null) {
            createRoom(room, context, db);
        } else {
            if (room.getLastUpdate().after(roomInDb.getLastUpdate())) {
                updateRoom(room, context, db);
            }
        }
    }

    public static boolean RoomExists(int roomId, Context context, SQLiteDatabase db) {
        Room roomInDb = getRoomById(roomId, db);
        return roomInDb != null;
    }

    @SuppressLint("Range")
    public List<Room> getAllRooms(SQLiteDatabase db) {
        List<Room> roomList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ROOM_TABLE, null);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        if (cursor.moveToFirst()) {
            try {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    Date lastUpdate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));

                    Room room = new Room(id, name, description, lastUpdate);
                    roomList.add(room);
                } while (cursor.moveToNext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return roomList;
    }

    @SuppressLint("Range")
    public static Room getRoomById(int roomId, SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("SELECT * FROM " + ROOM_TABLE + " WHERE " + COLUMN_ID + " = " + roomId, null);
        try {
            if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                Date lastUpdate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));

                return new Room(id, name, description, lastUpdate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
