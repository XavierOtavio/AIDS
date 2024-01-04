package com.pdm.aids.booking;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBBookingLocal extends SQLiteOpenHelper {
    public static final String BOOKING_TABLE = "BOOKING_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ROOM_ID = "ROOM_ID";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_BOOKING_STATUS_ID = "BOOKING_STATUS_ID";
    public static final String COLUMN_EXPECTED_START_DATE = "EXPECTED_START_DATE";
    public static final String COLUMN_EXPECTED_END_DATE = "EXPECTED_END_DATE";
    public static final String COLUMN_ACTUAL_START_DATE = "ACTUAL_START_DATE";
    public static final String COLUMN_ACTUAL_END_DATE = "ACTUAL_END_DATE";
    public static final String COLUMN_DATE_TIME = "DATE_TIME";
    public static final String COLUMN_HASH = "HASH";
    public static final String COLUMN_QR_CODE = "QR_CODE_IMAGE";


    public DBBookingLocal(@Nullable Context context) {
        super(context, "booking.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + BOOKING_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ROOM_ID + " INTEGER, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_BOOKING_STATUS_ID + " INTEGER, " +
                COLUMN_EXPECTED_START_DATE + " DATE, " +
                COLUMN_EXPECTED_END_DATE + " TEXT, " +
                COLUMN_ACTUAL_START_DATE + " TEXT, " +
                COLUMN_ACTUAL_END_DATE + " TEXT, " +
                COLUMN_DATE_TIME + " TEXT, " +
                COLUMN_HASH + " TEXT, " +
                COLUMN_QR_CODE + " BLOB" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BOOKING_TABLE);
        onCreate(db);
    }

    public void addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROOM_ID, booking.getRoomId());
        values.put(COLUMN_USER_ID, booking.getUserId());
        values.put(COLUMN_BOOKING_STATUS_ID, booking.getBookingStatusId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_EXPECTED_START_DATE, dateFormat.format(booking.getExpectedStartDate()));
        values.put(COLUMN_EXPECTED_END_DATE, dateFormat.format(booking.getExpectedEndDate()));
        values.put(COLUMN_ACTUAL_START_DATE, booking.getActualStartDate() != null ? dateFormat.format(booking.getActualStartDate()) : null);
        values.put(COLUMN_ACTUAL_END_DATE, booking.getActualEndDate() != null ? dateFormat.format(booking.getActualEndDate()) : null);
        values.put(COLUMN_DATE_TIME, dateFormat.format(booking.getDateTime()));

        values.put(COLUMN_HASH, booking.getHash());
        values.put(COLUMN_QR_CODE, booking.getQrImage(booking.getHash()));

        db.insert(BOOKING_TABLE, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public List<Booking> getAllBookings() {
        List<Booking> bookingsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + BOOKING_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));
                int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                int bookingStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_ID));
                String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date expectedStartDate, expectedEndDate, actualStartDate, actualEndDate, dateTime;
                try {
                    expectedStartDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_START_DATE)));
                    expectedEndDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_END_DATE)));
                    String actualStartDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_START_DATE));
                    actualStartDate = actualStartDateString != null ? dateFormat.parse(actualStartDateString) : null;
                    String actualEndDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_END_DATE));
                    actualEndDate = actualEndDateString != null ? dateFormat.parse(actualEndDateString) : null;
                    dateTime = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TIME)));

                    Booking booking = new Booking(roomId, userId, bookingStatusId, expectedStartDate,
                            expectedEndDate, actualStartDate, actualEndDate, dateTime, hash);

                    booking.setId(id);
                    booking.setQrImage(booking.getQrImage(hash));

                    bookingsList.add(booking);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookingsList;
    }
}
