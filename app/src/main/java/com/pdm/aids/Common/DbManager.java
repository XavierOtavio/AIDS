package com.pdm.aids.Common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Ticket.DBTicketLocal;

public class DbManager extends SQLiteOpenHelper {
    public static final String ROOM_TABLE = "ROOM_TABLE";
    public static final String BOOKING_TABLE = "BOOKING_TABLE";
    public static final String TICKET_TABLE = "TICKET_TABLE";

    public DbManager(@Nullable Context context) {
        super(context, "AIDS.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBRoomLocal.CreateTableRoom());
        db.execSQL(DBRoomImageLocal.CreateTableRoomImage());
        db.execSQL(DBBookingLocal.CreateTable());
        db.execSQL(DBTicketLocal.CreateTicketTable());
        db.execSQL(DBTicketLocal.CreateTicketImageTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
