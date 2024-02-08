package com.pdm.aids.Room;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class DBRoomImageLocal {
    public static final String ROOM_IMAGE_TABLE = "ROOM_IMAGE_TABLE";
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_FILE_NAME = "FILE_NAME";
    public static final String COLUMN_IMAGE_PATH = "IMAGE_PATH";
    public static final String COLUMN_ROOM_ID = "ROOM_ID";

    public static String CreateTableRoomImage() {
        return "CREATE TABLE IF NOT EXISTS " + ROOM_IMAGE_TABLE + " (" +
                COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_ROOM_ID + " INTEGER " +
                ");";
    }

    public static void addRoomImage(RoomImage roomImage, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_FILE_NAME, roomImage.getFileName());
        values.put(COLUMN_IMAGE_PATH, roomImage.getImagePath());
        values.put(COLUMN_ROOM_ID, roomImage.getRoomId());

        db.insert(ROOM_IMAGE_TABLE, null, values);
    }


    @SuppressLint("Range")
    public static Bitmap getRoomImageByRoomId(int roomId, SQLiteDatabase db) {
        Bitmap imageBytes = null;
        String query = "SELECT * FROM " + ROOM_IMAGE_TABLE + " WHERE " + COLUMN_ROOM_ID + " = " + roomId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    imageBytes = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                }
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return imageBytes;
    }

    public static boolean RoomImageExistsByFilePath(String imagePath, SQLiteDatabase db) {
        String query = "SELECT * FROM " + ROOM_IMAGE_TABLE + " WHERE " + COLUMN_IMAGE_PATH + " = '" + imagePath + "'";
        Cursor cursor = db.rawQuery(query, null);
        boolean exists = cursor.moveToFirst();
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return exists;
    }
}
