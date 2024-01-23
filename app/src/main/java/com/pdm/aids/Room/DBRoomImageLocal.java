package com.pdm.aids.Room;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));

                RoomImage roomImage = new RoomImage(imageId, fileName, imagePath, roomId);
                roomImageList.add(roomImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomImageList;
    }

    @SuppressLint("Range")
    public Bitmap getRoomImageByRoomId(int roomId, SQLiteDatabase db) {
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
}
