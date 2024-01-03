package com.pdm.aids.booking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBBookingLocal extends SQLiteOpenHelper {
    public static final String TICKET_TABLE = "BOOKING_TABLE";
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
        String createTableQuery = "CREATE TABLE " + TICKET_TABLE + " (" +
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
        db.execSQL("DROP TABLE IF EXISTS " + TICKET_TABLE);
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

        db.insert(TICKET_TABLE, null, values);
        db.close();
    }
}
