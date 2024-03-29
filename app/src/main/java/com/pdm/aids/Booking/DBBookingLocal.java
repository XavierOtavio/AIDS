package com.pdm.aids.Booking;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBBookingLocal {
    public static final String BOOKING_TABLE = "BOOKING_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ROOM_ID = "ROOM_ID";
    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_BOOKING_STATUS_ID = "BOOKING_STATUS_ID";
    public static final String COLUMN_EXPECTED_START_DATE = "EXPECTED_START_DATE";
    public static final String COLUMN_EXPECTED_END_DATE = "EXPECTED_END_DATE";
    public static final String COLUMN_ACTUAL_START_DATE = "ACTUAL_START_DATE";
    public static final String COLUMN_ACTUAL_END_DATE = "ACTUAL_END_DATE";
    public static final String COLUMN_LAST_UPDATE = "LAST_UPDATE";
    public static final String COLUMN_HASH = "HASH";

    public static String CreateTable() {
        return "CREATE TABLE IF NOT EXISTS " + BOOKING_TABLE + " (" +
                COLUMN_ID + " INTEGER, " +
                COLUMN_ROOM_ID + " INTEGER, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_BOOKING_STATUS_ID + " INTEGER, " +
                COLUMN_EXPECTED_START_DATE + " TEXT, " +
                COLUMN_EXPECTED_END_DATE + " TEXT, " +
                COLUMN_ACTUAL_START_DATE + " TEXT, " +
                COLUMN_ACTUAL_END_DATE + " TEXT, " +
                COLUMN_LAST_UPDATE + " TEXT, " +
                COLUMN_HASH + " TEXT " +
                ");";
    }

    public static void createBooking(Booking booking, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, booking.getId());
        values.put(COLUMN_ROOM_ID, booking.getRoomId());
        values.put(COLUMN_USER_ID, booking.getUserId());
        values.put(COLUMN_BOOKING_STATUS_ID, booking.getBookingStatusId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_EXPECTED_START_DATE, dateFormat.format(booking.getExpectedStartDate()));
        values.put(COLUMN_EXPECTED_END_DATE, dateFormat.format(booking.getExpectedEndDate()));
        values.put(COLUMN_ACTUAL_START_DATE, booking.getActualStartDate() != null ? dateFormat.format(booking.getActualStartDate()) : null);
        values.put(COLUMN_ACTUAL_END_DATE, booking.getActualEndDate() != null ? dateFormat.format(booking.getActualEndDate()) : null);
        values.put(COLUMN_LAST_UPDATE, dateFormat.format(booking.getLast_modified()));

        values.put(COLUMN_HASH, booking.getHash());

        db.insert(BOOKING_TABLE, null, values);
    }

    public static void updateBooking(Booking booking, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROOM_ID, booking.getRoomId());
        values.put(COLUMN_USER_ID, booking.getUserId());
        values.put(COLUMN_BOOKING_STATUS_ID, booking.getBookingStatusId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COLUMN_EXPECTED_START_DATE, dateFormat.format(booking.getExpectedStartDate()));
        values.put(COLUMN_EXPECTED_END_DATE, dateFormat.format(booking.getExpectedEndDate()));
        values.put(COLUMN_ACTUAL_START_DATE, booking.getActualStartDate() != null ? dateFormat.format(booking.getActualStartDate()) : null);
        values.put(COLUMN_ACTUAL_END_DATE, booking.getActualEndDate() != null ? dateFormat.format(booking.getActualEndDate()) : null);
        values.put(COLUMN_LAST_UPDATE, dateFormat.format(booking.getLast_modified()));

        values.put(COLUMN_HASH, booking.getHash());

        db.update(BOOKING_TABLE, values, COLUMN_ID + " = " + booking.getId(), null);
    }

    public static void deleteBooking(int id, SQLiteDatabase db) {
        db.delete(BOOKING_TABLE, COLUMN_ID + " = " + id, null);
    }

    public static void createOrUpdateBooking(Booking booking, SQLiteDatabase db) {
        Booking foundBooking = getBookingByHash(booking.getHash(), db);
        if (foundBooking == null) {
            createBooking(booking, db);
        } else {
            if (foundBooking.getLast_modified().before(booking.getLast_modified())) {
                booking.setId(foundBooking.getId());
                updateBooking(booking, db);
            }
        }
    }

    @SuppressLint("Range")
    public List<Booking> getAllBookings(SQLiteDatabase db) {
        List<Booking> bookingsList = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + BOOKING_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date expectedStartDate, expectedEndDate;
                    String actualStartDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_START_DATE));
                    String actualEndDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_END_DATE));

                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));
                    int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                    int bookingStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_ID));
                    expectedStartDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_START_DATE)));
                    expectedEndDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_END_DATE)));
                    Date actualStartDate = actualStartDateString != null ? dateFormat.parse(actualStartDateString) : null;
                    Date actualEndDate = actualEndDateString != null ? dateFormat.parse(actualEndDateString) : null;
                    Date dateTime = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));
                    String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));

                    Booking booking = new Booking(roomId, userId, bookingStatusId, expectedStartDate,
                            expectedEndDate, actualStartDate, actualEndDate, dateTime, hash);
                    booking.setId(id);
                    bookingsList.add(booking);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookingsList;
    }

    @SuppressLint("Range")
    public static Booking getBookingByHash(String hash, SQLiteDatabase db) {
        String selectQuery = "SELECT * FROM " + BOOKING_TABLE + " WHERE " + COLUMN_HASH + " = '" + hash + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date expectedStartDate, expectedEndDate;
                String actualStartDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_START_DATE));
                String actualEndDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_END_DATE));

                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));
                int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                int bookingStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_ID));
                expectedStartDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_START_DATE)));
                expectedEndDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_END_DATE)));
                Date actualStartDate = actualStartDateString != null ? dateFormat.parse(actualStartDateString) : null;
                Date actualEndDate = actualEndDateString != null ? dateFormat.parse(actualEndDateString) : null;
                Date dateTime = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));
                String hashBooking = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));

                cursor.close();
                return new Booking(id, roomId, userId, bookingStatusId, expectedStartDate,
                        expectedEndDate, actualStartDate, actualEndDate, dateTime, hashBooking);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public List<Booking> getBookingsByStatus(List<Integer> bookingStatusIds, SQLiteDatabase db) {
        List<Booking> bookingsList = new ArrayList<>();

        StringBuilder whereClause = new StringBuilder();
        if (bookingStatusIds.size() == 1) {
            whereClause.append(COLUMN_BOOKING_STATUS_ID).append(" = ").append(bookingStatusIds.get(0));
        } else if (bookingStatusIds.size() > 1) {
            whereClause.append(COLUMN_BOOKING_STATUS_ID).append(" IN (")
                    .append(TextUtils.join(",", bookingStatusIds)).append(")");
        }

        String selectQuery = "SELECT * FROM " + BOOKING_TABLE;
        if (whereClause.length() > 0) {
            selectQuery += " WHERE " + whereClause.toString();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date expectedStartDate, expectedEndDate;
                    String actualStartDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_START_DATE));
                    String actualEndDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_END_DATE));

                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));
                    int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                    int bookingStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_ID));
                    expectedStartDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_START_DATE)));
                    expectedEndDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_END_DATE)));
                    Date actualStartDate = actualStartDateString != null ? dateFormat.parse(actualStartDateString) : null;
                    Date actualEndDate = actualEndDateString != null ? dateFormat.parse(actualEndDateString) : null;
                    Date dateTime = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));
                    String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));

                    Booking booking = new Booking(roomId, userId, bookingStatusId, expectedStartDate,
                            expectedEndDate, actualStartDate, actualEndDate, dateTime, hash);
                    booking.setId(id);
                    bookingsList.add(booking);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookingsList;
    }

    //get the booking that the current date is between the expected start and end date
    @SuppressLint("Range")
    public Booking getCurrentAvaliableBooking(SQLiteDatabase db) {
        Booking booking = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String selectQuery = "SELECT * FROM " + BOOKING_TABLE + " WHERE datetime('now') BETWEEN " + COLUMN_EXPECTED_START_DATE + " AND " + COLUMN_EXPECTED_END_DATE + " LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            try {
                Date expectedStartDate, expectedEndDate;
                String actualStartDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_START_DATE));
                String actualEndDateString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTUAL_END_DATE));

                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                int roomId = cursor.getInt(cursor.getColumnIndex(COLUMN_ROOM_ID));
                int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                int bookingStatusId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_STATUS_ID));
                expectedStartDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_START_DATE)));
                expectedEndDate = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_EXPECTED_END_DATE)));
                Date actualStartDate = actualStartDateString != null ? dateFormat.parse(actualStartDateString) : null;
                Date actualEndDate = actualEndDateString != null ? dateFormat.parse(actualEndDateString) : null;
                Date dateTime = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_UPDATE)));
                String hash = cursor.getString(cursor.getColumnIndex(COLUMN_HASH));

                booking = new Booking(roomId, userId, bookingStatusId, expectedStartDate,
                        expectedEndDate, actualStartDate, actualEndDate, dateTime, hash);
                booking.setId(id);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return booking;
    }

    //get current boking with status = 3
    public Booking getCurrentOnGoingBooking(SQLiteDatabase db) {
        List<Integer> status = new ArrayList<>();
        status.add(3);
        List<Booking> bookings = getBookingsByStatus(status, db);
        if (bookings.size() > 0) {
            return bookings.get(0);
        }
        return null;
    }

}
