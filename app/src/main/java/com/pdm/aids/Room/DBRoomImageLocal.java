package com.pdm.aids.Room;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBRoomImageLocal {
    public static final String ROOM_IMAGE_TABLE = "ROOM_IMAGE_TABLE";
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_FILE_NAME = "FILE_NAME";
    public static final String COLUMN_IMAGE = "IMAGE";
    public static final String COLUMN_ROOM_ID = "ROOM_ID";

    public static String CreateTableRoomImage() {
        return "CREATE TABLE IF NOT EXISTS " + ROOM_IMAGE_TABLE + " (" +
                COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_ROOM_ID + " INTEGER " +
                ");";
    }

    public void addRoomImage(RoomImage roomImage, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_FILE_NAME, roomImage.getFileName());
        values.put(COLUMN_IMAGE, roomImage.getImageBytes());
        values.put(COLUMN_ROOM_ID, roomImage.getRoomId());

        db.insert(ROOM_IMAGE_TABLE, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<RoomImage> getAllRoomImages(SQLiteDatabase db) {
        List<RoomImage> roomImageList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ROOM_IMAGE_TABLE, null);
        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                String fileName = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));

                RoomImage roomImage = new RoomImage(imageId, fileName, imageBytes, roomId);
                roomImageList.add(roomImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomImageList;
    }

    @SuppressLint("Range")
    public List<RoomImage> getRoomImagesByRoomId(int roomId, SQLiteDatabase db) {
        List<RoomImage> roomImageList = new ArrayList<>();

        Cursor cursor = db.query(ROOM_IMAGE_TABLE,
                new String[]{COLUMN_IMAGE_ID, COLUMN_FILE_NAME, COLUMN_IMAGE, COLUMN_ROOM_ID},
                COLUMN_ROOM_ID + "=?",
                new String[]{String.valueOf(roomId)},
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                String fileName = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                int roomID = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));

                RoomImage roomImage = new RoomImage(imageId, fileName, imageBytes, roomID);
                roomImageList.add(roomImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomImageList;
    }
}
