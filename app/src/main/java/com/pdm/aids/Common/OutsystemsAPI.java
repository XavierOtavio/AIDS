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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


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

    public interface RoomCallback {
        void onRoomsReceived(Room room);

        void onError(String error);
    }

    public interface RoomImageCallback {
        void onRoomImageReceived(RoomImage roomImage);

        void onError(String error);
    }

    public interface TicketCallback {
        void onTicketReceived(ArrayList<Ticket> ticketArrayList);
    }

    public interface OnlineTicketCallback {
        void onTicketImageReceived(Ticket ticket, Room room, ArrayList<TicketImage> ticketImageArrayList);

        void onError(String error);
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

//############################################################//
//                                                            //
//            ______     _         _                          //
//           |  ____|   | |       | |                         //
//           | |__  ___ | |_  ___ | |__    ___  ___           //
//           |  __|/ _ \| __|/ __|| '_ \  / _ \/ __|          //
//           | |  |  __/| |_| (__ | | | ||  __/\__ \          //
//           |_|   \___| \__|\___||_| |_| \___||___/          //
//                                                            //
//############################################################//


    @SuppressLint("Range")
    public static void getDataFromAPI(String userId, Context context, final DataLoadCallback finalCallback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        final int[] pendingTasks = {6};
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
        getRoomsINeed(userId, context, db, dbManager, volleyCallback);
        getBookingsByUser(userId, context, db, dbManager, volleyCallback);
        getRoomsINeed(userId, context, db, dbManager, volleyCallback);
        getRoomImages(userId, context, db, dbManager, volleyCallback);
        getTicketsByUser(userId, context, db, dbManager, volleyCallback);
        getTicketImages(userId, context, db, dbManager, volleyCallback);
    }

    @SuppressLint("Range")
    public static void RefreshBookings(String userId, Context context, final DataLoadCallback finalCallback) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        final int[] pendingTasks = {3};
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
        submitPendingTickets(userId, context, db, dbManager, volleyCallback);
    }

    @SuppressLint("Range")
    public static void RefreshBookingDetail(String userId, String bookingUUID, int bookingId, Context context, final DataLoadCallback finalCallback) {
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
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            IdList = new ArrayList<>();
            do {
                IdList.add(cursor.getInt(cursor.getColumnIndex("ID")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        checkBookingStatus(IdList, context, db, dbManager, volleyCallback);
        submitPendingTickets(userId, context, db, dbManager, volleyCallback);
        getBookingById(bookingId, context, db, dbManager, volleyCallback);
        getTicketsByBookingUUID(bookingUUID, context, db, dbManager, volleyCallback);
    }

//############################################################//
//                                                            //
//         ____                 _     _                       //
//        |  _ \               | |   (_)                      //
//        | |_) |  ___    ___  | | __ _  _ __    __ _         //
//        |  _ <  / _ \  / _ \ | |/ /| || '_ \  / _` |        //
//        | |_) || (_) || (_) ||   < | || | | || (_| |        //
//        |____/  \___/  \___/ |_|\_\|_||_| |_| \__, |        //
//                                               __/ |        //
//                                              |___/         //
//                                                            //
//############################################################//

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
                                String idsToDelete = String.join("\",\"", response.getString("IdsToDelete").split(","));
                                db.execSQL("DELETE FROM " + dbManager.BOOKING_TABLE + " WHERE HASH IN (\"" + idsToDelete + "\")");
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

    public static void getBookingById(int bookingId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetBookingById?BookingId=" + bookingId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONObject bookingObj = new JSONObject(obj.getString("Booking"));
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
                                    bookingObj.getString("UUID"));
                            DBBookingLocal.createOrUpdateBooking(booking, db);
                            callback.onSuccess("Booking fetched successfully");
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (
                            JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (
                            ParseException e) {
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
                            if (bookingList != null) {
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
                                        bookingObj.getInt("Id"),
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

//############################################################//
//             _____                                          //
//            |  __ \                                         //
//            | |__) | ___    ___   _ __ ___   ___            //
//            |  _  / / _ \  / _ \ | '_ ` _ \ / __|           //
//            | | \ \| (_) || (_) || | | | | |\__ \           //
//            |_|  \_\\___/  \___/ |_| |_| |_||___/           //
//                                                            //
//############################################################//


    public static void getRoomImageOnline(int RoomId, Context context, RoomImageCallback callback) {
        String url = apiUrl + "GetRoomImageByRoom?RoomId=" + RoomId;
        RoomImage roomImage = new RoomImage();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONObject imageObj = new JSONObject(obj.getString("RoomImage"));
                            roomImage.setImageId(imageObj.getInt("Id"));
                            roomImage.setFileName(imageObj.getString("Filename"));
                            roomImage.setImageBitmap(Utils.imageConvert(imageObj.getString("Image")));
                            roomImage.setRoomId(imageObj.getInt("RoomId"));
                            roomImage.setLast_modified(Utils.convertUnixToDate(imageObj.getString("ModifiedOn")));
                            callback.onRoomImageReceived(roomImage);
                        } else {
                            callback.onError(obj.getString("Message"));
                        }
                    } catch (JSONException e) {
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

    public static void getRoomById(int roomId, Context context, RoomCallback callback) {
        String url = apiUrl + "GetRoomById?RoomId=" + roomId;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            Room room = new Room();
                            JSONArray roomList = new JSONArray(obj.getString("RoomList"));
                            for (int i = 0; i < roomList.length(); i++) {
                                JSONObject roomListJSONObject = roomList.getJSONObject(i);
                                room = new Room(
                                        roomListJSONObject.getInt("Id"),
                                        roomListJSONObject.getString("Name"),
                                        roomListJSONObject.getString("Description"),
                                        Utils.convertUnixToDate(roomListJSONObject.getString("ModifiedOn"))
                                );
                            }
                            callback.onRoomsReceived(room);
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            try {
                JSONObject obj = new JSONObject(Objects.requireNonNull(error.getMessage()));
                Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    public static void getRoomImages(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        String url = apiUrl + "GetRoomImagesUserNeeds?UserId=" + userId;

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
                                Room room = new Room(
                                        roomObj.getInt("Id"),
                                        roomObj.getString("Name"),
                                        roomObj.getString("Description"),
                                        Utils.convertUnixToDate(roomObj.getString("ModifiedOn"))
                                );
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
                            Booking booking = new Booking(
                                    obj.getInt("Id"),
                                    obj.getInt("RoomId"),
                                    obj.getInt("ReservedBy"),
                                    obj.getInt("BookingStatusId"),
                                    Utils.convertUnixToDate(obj.getString("ExpectedStartDate")),
                                    Utils.convertUnixToDate(obj.getString("ExpectedEndDate")),
                                    Utils.convertUnixToDate(obj.getString("ActualStartDate")),
                                    Utils.NULL_DATE,
                                    Utils.convertUnixToDate(obj.getString("ModifiedOn")),
                                    obj.getString("UUID")
                            );
                            DBBookingLocal.createOrUpdateBooking(booking, db);
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


//############################################################//
//            _______  _        _          _                  //
//           |__   __|(_)      | |        | |                 //
//              | |    _   ___ | | __ ___ | |_  ___           //
//              | |   | | / __|| |/ // _ \| __|/ __|          //
//              | |   | || (__ |   <|  __/| |_ \__ \          //
//              |_|   |_| \___||_|\_\\___| \__||___/          //
//                                                            //
//############################################################//


    public static void getTicketById(String UUID, Context context, SQLiteDatabase db, OnlineTicketCallback callback) {
        String url = apiUrl + "GetTicketById?TicketUUID=" + UUID;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            JSONObject ticketObj = new JSONObject(obj.getString("Ticket"));
                            Ticket ticket = new Ticket(
                                    ticketObj.getString("TicketUUID"),
                                    ticketObj.getString("BookingUUID"),
                                    true,
                                    ticketObj.getString("Title"),
                                    ticketObj.getString("Description"),
                                    Utils.convertUnixToDate(ticketObj.getString("CreatedOn")),
                                    Utils.convertUnixToDate(ticketObj.getString("ModifiedOn"))
                            );
                            JSONObject roomObj = new JSONObject(obj.getString("Room"));
                            Room room = new Room(
                                    roomObj.getInt("Id"),
                                    roomObj.getString("Name"),
                                    roomObj.getString("Description"),
                                    Utils.convertUnixToDate(roomObj.getString("ModifiedOn"))
                            );
                            ArrayList<TicketImage> ticketImageArrayList = new ArrayList<>();
                            JSONArray ticketImageList = new JSONArray(obj.getString("TicketImagesList"));
                            for (int i = 0; i < ticketImageList.length(); i++) {
                                JSONObject ticketImageObj = ticketImageList.getJSONObject(i);
                                if(ticketImageObj.getInt("Id") == 0) {
                                    continue;
                                }
                                TicketImage ticketImage = new TicketImage(
                                        ticketImageObj.getString("TicketUUID"),
                                        ticketImageObj.getString("Filename"),
                                        ticketImageObj.getString("Image"),
                                        Utils.convertUnixToDate(ticketImageObj.getString("ModifiedOn"))
                                );
                                ticketImageArrayList.add(ticketImage);
                            }
                            callback.onTicketImageReceived(ticket, room, ticketImageArrayList);
                        } else {
                            callback.onError(obj.getString("Message"));
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


    @SuppressLint("Range")
    public static void submitPendingTickets(String userId, Context context, SQLiteDatabase db, DbManager dbManager, final VolleyCallback callback) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + dbManager.TICKET_TABLE + " WHERE IS_SYNCHRONIZED = 0", null);
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            do {
                ArrayList<TicketImage> ticketImageArrayList = DBTicketLocal.getTicketImageByTicketId(cursor.getString(cursor.getColumnIndex("TICKET_ID")), db);
                Ticket ticket = new Ticket(
                        cursor.getString(cursor.getColumnIndex("TICKET_ID")),
                        cursor.getString(cursor.getColumnIndex("BOOKING_ID")),
                        false,
                        cursor.getString(cursor.getColumnIndex("TITLE")),
                        cursor.getString(cursor.getColumnIndex("DESCRIPTION")),
                        Utils.convertStringToDate(cursor.getString(cursor.getColumnIndex("TICKET_STARTDATE"))),
                        Utils.convertStringToDate(cursor.getString(cursor.getColumnIndex("TICKET_MODIFIED"))),
                        ticketImageArrayList
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
            ticketJsonObject.put("Ticket", new JSONObject(ticket.toJsonWithoutImages(Integer.parseInt(userId))));
            ticketJsonObject.put("TicketImages", new JSONArray(ticket.toJsonImagesOnly(db)));

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
                if (error.getMessage() != null) {
                    JSONObject obj = new JSONObject(error.getMessage());
                    Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(stringRequest);
    }

    public static void getTicketsByBookingUUIDOnline(String bookingUUID, Context context, TicketCallback callback) {
        String url = apiUrl + "GetTicketsByBooking?BookingUUID=" + bookingUUID;
        ArrayList<Ticket> ticketArrayList = new ArrayList<>();

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
                                ticketArrayList.add(ticket);
                            }
                            callback.onTicketReceived(ticketArrayList);
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
                            JSONArray imageList = new JSONArray(obj.getString("TicketImageList"));
                            for (int i = 0; i < imageList.length(); i++) {
                                JSONObject imageObj = imageList.getJSONObject(i);
                                String filePath = Utils.addImageToLocalStorage("TicketImages", imageObj, context);

                                TicketImage ticketImage = new TicketImage();
                                ticketImage.setFilename(imageObj.getString("Filename"));
                                ticketImage.setImagePath(filePath);
                                ticketImage.setTicketUuid((imageObj.getString("TicketUUID")));
                                ticketImage.setIsUploaded(true);
                                ticketImage.setLast_modified(Utils.convertUnixToDate(imageObj.getString("ModifiedOn")));

                                DBTicketLocal.createOrUpdateTicketImage(ticketImage, context, db);
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
