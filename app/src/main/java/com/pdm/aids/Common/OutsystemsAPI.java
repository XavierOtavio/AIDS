package com.pdm.aids.Common;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Room.RoomImage;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketDetails.CreateTicketActivity;
import com.pdm.aids.Ticket.TicketImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class OutsystemsAPI extends AppCompatActivity {
    private String id;
    private static List<Booking> bookings;

    public interface VolleyCallback {
        void onSuccess(String result) throws ParseException;

        void onError(String error);
    }

    public interface DataLoadCallback {
        void onDataLoaded();

        void onError(String error);
    }

    public interface BookingCallback {
        void onBookingsReceived(ArrayList<Booking> bookingArrayList);
    }

    static String apiUrl = "https://personal-8o07igno.outsystemscloud.com/AIDS/rest/RestAPI/";

    public static void checkLogin(String username, String password, Context context, VolleyCallback callback) {
        String url = apiUrl + "CheckLogin?Username=" + username + "&Password=" + password;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            callback.onError(error.getMessage());
        }
        );
        queue.add(stringRequest);
    }

    public static void registerUser(String name, String username, String password, Context context, VolleyCallback callback) {
        String url = apiUrl + "CreateUser?Name=" + name + "&Username=" + username + "&Password=" + password;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            callback.onError(error.getMessage());
        }
        );
        queue.add(stringRequest);
    }

    @SuppressLint("Range")
    public static void getDataFromAPI(String userId, Context context, final DataLoadCallback finalCallback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        final int[] pendingTasks = {4};
        final String[] errorMessages = {null};

        VolleyCallback volleyCallback = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                synchronized (pendingTasks) {
                    pendingTasks[0]--;
                    if (pendingTasks[0] == 0 && errorMessages[0] == null) {
                        finalCallback.onDataLoaded();
                    }
                }
            }

            @Override
            public void onError(String error) {
                synchronized (pendingTasks) {
                    if (errorMessages[0] == null) { // Report first error encountered
                        errorMessages[0] = error;
                        finalCallback.onError(error);
                    }
                }
            }
        };

        ArrayList<Integer> IdList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT ID FROM " + dbManager.BOOKING_TABLE, null);
        if (cursor.moveToFirst()) {
            IdList = new ArrayList<>();
            do {
                IdList.add(cursor.getInt(cursor.getColumnIndex("ID")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        checkBookingStatus(IdList, context, db, dbManager, volleyCallback);
        getBookingsByUser(userId, context, db, dbManager, volleyCallback);
        getRoomImages(userId, context, db, dbManager, volleyCallback);
        getTicketsByUser(userId, context, db, dbManager, volleyCallback);
//        getTicketImages(userId, context, db, dbManager, volleyCallback);
    }

    public static void RefreshBookings(String userId, Context context, final DataLoadCallback finalCallback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        final int[] pendingTasks = {2};
        final String[] errorMessages = {null};

        VolleyCallback volleyCallback = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                synchronized (pendingTasks) {
                    pendingTasks[0]--;
                    if (pendingTasks[0] == 0 && errorMessages[0] == null) {
                        finalCallback.onDataLoaded();
                    }
                }
            }

            @Override
            public void onError(String error) {
                synchronized (pendingTasks) {
                    if (errorMessages[0] == null) { // Report first error encountered
                        errorMessages[0] = error;
                        finalCallback.onError(error);
                    }
                }
            }
        };

        getBookingsByUser(userId, context, db, dbManager, volleyCallback);
        submitPendingTickets(userId, context, db, dbManager, volleyCallback);
    }

    public static void RefreshBookingDetail(String userId, String bookingUUID, Context context, final DataLoadCallback finalCallback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        final int[] pendingTasks = {2};
        final String[] errorMessages = {null};

        VolleyCallback volleyCallback = new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                synchronized (pendingTasks) {
                    pendingTasks[0]--;
                    if (pendingTasks[0] == 0 && errorMessages[0] == null) {
                        finalCallback.onDataLoaded();
                    }
                }
            }

            @Override
            public void onError(String error) {
                synchronized (pendingTasks) {
                    if (errorMessages[0] == null) { // Report first error encountered
                        errorMessages[0] = error;
                        finalCallback.onError(error);
                    }
                }
            }
        };

        getBookingsByUser(userId, context, db, dbManager, volleyCallback);
        getTicketsByBookingUUID(bookingUUID, context, db, dbManager, volleyCallback);
    }

    public static void checkBookingStatus(ArrayList<Integer> bookingIds, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "CheckBookingStatus";

        try {
            JSONObject bookingIdsJson = new JSONObject();
            String str = Arrays.toString(bookingIds.toArray()).replace("[", "").replace("]", "");
            bookingIdsJson.put("IdList", String.join(",", str));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, bookingIdsJson,
                    response -> {
                        try {
                            if (response.getString("HTTPCode").equals("200")) {
                                db.execSQL("DELETE FROM " + dbManager.BOOKING_TABLE + " WHERE HASH IN (\"" + response.getString("IdsToDelete") + "\")");
                                callback.onSuccess("Booking status checked successfully");
                            } else {
                                callback.onError(response.getString("Message"));
                            }
                        } catch (JSONException e) {
                            callback.onError("Error parsing response");
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> callback.onError("Error submitting ticket: " + error.getMessage()));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            callback.onError("Error converting Ticket to JSON " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getBookingsByUser(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetMyBookingsByUser?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            db.execSQL("DELETE FROM " + dbManager.BOOKING_TABLE + " WHERE NOT USER_ID = " + userId);
                            JSONArray bookingList = new JSONArray(obj.getString("BookingList"));

                            for (int i = 0; i < bookingList.length(); i++) {
                                JSONObject bookingObj = bookingList.getJSONObject(i);
                                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                                Booking booking = new Booking(
                                        bookingObj.getInt("Id"),
                                        bookingObj.getInt("RoomId"),
                                        bookingObj.getInt("ReservedBy"),
                                        bookingObj.getInt("BookingStatusId"),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ModifiedOn")),
                                        bookingObj.getString("UUID")
                                );
                                DBBookingLocal.createOrUpdateBooking(booking, db);
                            }
                            callback.onSuccess("Bookings fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }


    public static void getBookingsHistory(String userId, Context context, BookingCallback callback) {
        String url = apiUrl + "GetBookingHistory?UserId=" + userId;
        ArrayList<Booking> bookingArrayList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONArray bookingList = new JSONArray(obj.getString("BookingList"));

                            for (int i = 0; i < bookingList.length(); i++) {
                                JSONObject bookingObj = bookingList.getJSONObject(i);
                                Booking booking = new Booking(
                                        bookingObj.getInt("RoomId"),
                                        bookingObj.getInt("ReservedBy"),
                                        bookingObj.getInt("BookingStatusId"),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ModifiedOn")),
                                        bookingObj.getString("UUID"));
                                bookingArrayList.add(booking);
                            }

                            callback.onBookingsReceived(bookingArrayList);
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public static void getRoomImages(String roomId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetRoomImagesUserNeeds?UserId=" + roomId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONArray imageList = new JSONArray(obj.getString("RoomImageList"));
                            for (int i = 0; i < imageList.length(); i++) {
                                JSONObject imageObj = imageList.getJSONObject(i);
                                String filePath = Utils.addImageToLocalStorage("RoomImages", imageObj, context);
                                if (!DBRoomImageLocal.RoomImageExistsByFilePath(filePath, db)) {
                                    RoomImage roomImage = new RoomImage();
                                    roomImage.setFileName(imageObj.getString("Filename"));
                                    roomImage.setImagePath(filePath);
                                    roomImage.setRoomId(Integer.parseInt(imageObj.getString("RoomId")));

                                    DBRoomImageLocal.addRoomImage(roomImage, db);
                                }
                            }
                            callback.onSuccess("Room Images fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }

    public static void getRoomsINeed(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetRoomsUserNeeds?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONArray roomList = new JSONArray(obj.getString("RoomList"));

                            for (int i = 0; i < roomList.length(); i++) {
                                JSONObject roomObj = roomList.getJSONObject(i);
                                Room room = new Room(roomObj.getInt("Id"), roomObj.getString("Name"), roomObj.getString("Description"));
                                DBRoomLocal.createOrUpdateRoom(room, context, db);
                            }
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    try {
                        callback.onSuccess("Rooms fetched successfully");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }

    public static void validateEntry(String hash, String userId, String roomId, Context context, VolleyCallback callback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        String url = apiUrl + "ValidateEntry?Hash=" + hash + "&UserId=" + userId + "&RoomId=" + roomId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            db.execSQL("DELETE FROM " + dbManager.BOOKING_TABLE + " WHERE Hash = '" + hash + "'");

                            Booking booking = new Booking(
                                    obj.getInt("RoomId"),
                                    obj.getInt("ReservedBy"),
                                    obj.getInt("BookingStatusId"),
                                    Utils.convertUnixToDate(obj.getString("ExpectedStartDate")),
                                    Utils.convertUnixToDate(obj.getString("ExpectedEndDate")),
                                    Utils.convertUnixToDate(obj.getString("ActualStartDate")),
                                    Utils.convertUnixToDate(obj.getString("-2208988800")),
                                    Utils.convertUnixToDate(obj.getString("ModifiedOn")),
                                    obj.getString("UUID")
                            );
                            DBBookingLocal.createBooking(booking, db);
                            callback.onSuccess("Validation successful");
                        } else {
                            callback.onError(obj.getString("Message"));
                        }
                    } catch (JSONException e) {
                        callback.onError(e.getMessage());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    try {
                        JSONObject obj = new JSONObject(error.getMessage());
                        callback.onError(obj.getString("Message"));
                    } catch (JSONException e) {
                        callback.onError(e.getMessage());
                    }
                }
        );

        queue.add(stringRequest);
    }

    @SuppressLint("Range")
    public static void submitPendingTickets(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbManager.TICKET_TABLE + " WHERE IS_SYNCHRONIZED = 0", null);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                Ticket ticket = new Ticket(
                        cursor.getString(cursor.getColumnIndex("TICKET_ID")),
                        cursor.getString(cursor.getColumnIndex("BOOKING_ID")),
                        false,
                        cursor.getString(cursor.getColumnIndex("TITLE")),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION")),
                        Utils.convertStringToDate(cursor.getString(cursor.getColumnIndex("TICKET_STARTDATE"))),
                        Utils.convertStringToDate(cursor.getString(cursor.getColumnIndex("TICKET_MODIFIED")))
                );
                submitTicket(ticket, userId, context, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) throws ParseException {
                        db.execSQL("UPDATE " + dbManager.TICKET_TABLE + " SET IS_SYNCHRONIZED = 1 WHERE TICKET_ID = '" + ticket.getId() + "'");
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });

            } while (cursor.moveToNext());
        }
        try {
            callback.onSuccess("All tickets submitted successfully");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        cursor.close();
    }

    public static void submitTicket(Ticket ticket, String userId, Context context, VolleyCallback callback) {
        String url = apiUrl + "SubmitTicket";
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        try {
            JSONObject ticketJsonObject = new JSONObject();
            ticketJsonObject.put("tickets", new JSONObject(ticket.toJsonWithoutImages(Integer.parseInt(userId))));
            ticketJsonObject.put("ticketImages", new JSONArray(ticket.toJsonImagesOnly(db)));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, ticketJsonObject, response -> {
                try {
                    if (response.getString("HTTPCode").equals("200")) {
                        callback.onSuccess("All good! " + response.getString("Message"));
                    } else {
                        callback.onError("Error in images but " + response.getString("Message"));
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing response");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }, error -> callback.onError("Error submitting ticket: " + error.getMessage()));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            callback.onError("Error converting Ticket to JSON " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getTicketsByUser(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetTicketsByUser?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONArray ticketList = new JSONArray(obj.getString("TicketList"));

                            for (int i = 0; i < ticketList.length(); i++) {
                                JSONObject ticketObj = ticketList.getJSONObject(i);
                                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                                Ticket ticket = new Ticket(
                                        ticketObj.getString("TicketUUID"),
                                        ticketObj.getString("BookingUUID"),
                                        true,
                                        ticketObj.getString("Title"),
                                        ticketObj.getString("Description"),
                                        Utils.convertUnixToDate(ticketObj.getString("CreatedOn")),
                                        Utils.convertUnixToDate(ticketObj.getString("ModifiedOn"))
                                );
                                DBTicketLocal.createOrUpdateTicket(ticket, context, db);
                            }
                            callback.onSuccess("Tickets fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }

    public static void getTicketsByBookingUUID(String bookingUUID, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetTicketsByBooking?BookingUUID=" + bookingUUID;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONArray ticketList = new JSONArray(obj.getString("TicketList"));

                            for (int i = 0; i < ticketList.length(); i++) {
                                JSONObject ticketObj = ticketList.getJSONObject(i);
                                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                                Ticket ticket = new Ticket(
                                        ticketObj.getString("TicketUUID"),
                                        ticketObj.getString("BookingUUID"),
                                        true,
                                        ticketObj.getString("Title"),
                                        ticketObj.getString("Description"),
                                        Utils.convertUnixToDate(ticketObj.getString("CreatedOn")),
                                        Utils.convertUnixToDate(ticketObj.getString("ModifiedOn"))
                                );
                                DBTicketLocal.createOrUpdateTicket(ticket, context, db);
                            }
                            callback.onSuccess("Tickets fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }

    public static void getTicketImages(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetTicketImagesUserNeeds?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            //TODO: ISTO NÃO PODE ACONTECER. OS TICKETS PODEM AINDA NÃO TEREM SIDO SINCRONIZADOS
                            db.execSQL("DELETE FROM " + dbManager.TICKET_IMAGE_TABLE);
                            JSONArray imageList = new JSONArray(obj.getString("TicketImageList"));
                            for (int i = 0; i < imageList.length(); i++) {
                                JSONObject imageObj = imageList.getJSONObject(i);
                                String filePath = Utils.addImageToLocalStorage("TicketImages", imageObj, context);

                                TicketImage ticketImage = new TicketImage();
                                ticketImage.setFilename(imageObj.getString("Filename"));
                                ticketImage.setImagePath(filePath);
                                ticketImage.setTicketUuid((imageObj.getString("TicketUUID")));

                                DBTicketLocal.createTicketImage(ticketImage, context, db);
                            }
                            callback.onSuccess("Ticket Images fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }
}
