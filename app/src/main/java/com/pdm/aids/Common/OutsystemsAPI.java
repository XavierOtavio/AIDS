package com.pdm.aids.Common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OutsystemsAPI extends AppCompatActivity {
    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(String error);
    }

    static String apiUrl = "https://personal-8o07igno.outsystemscloud.com/AIDS/rest/RestAPI/";

    public static void checkLogin(String username, String password, Context context, VolleyCallback callback) {
        String url = apiUrl + "CheckLogin?Username=" + username + "&Password=" + password;

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    callback.onSuccess(response);
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
                    callback.onSuccess(response);
                }, error -> {
            callback.onError(error.getMessage());
        }
        );
        queue.add(stringRequest);
    }


    public static void getDataFromAPI(String userId, Context context) {
        DbManager dbManager = new DbManager(context);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        //Get bookings by user
        getBookingsByUser(userId, context, db, dbManager);
        //Get rooms
        getRoomsINeed(userId, context, db, dbManager);
    }

    public static void getBookingsByUser(String userId, Context context, SQLiteDatabase db, DbManager dbManager) {
        String url = apiUrl + "GetMyBookingsByUser?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            db.execSQL("DELETE FROM " + dbManager.BOOKING_TABLE);
                            JSONArray bookingList = new JSONArray(obj.getString("BookingList"));

                            for (int i = 0; i < bookingList.length(); i++) {
                                JSONObject bookingObj = bookingList.getJSONObject(i);
                                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                                Booking booking = new Booking(
                                        bookingObj.getInt("RoomId"),
                                        bookingObj.getInt("ReservedBy"),
                                        bookingObj.getInt("BookingStatusId"),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ExpectedEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualStartDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ActualEndDate")),
                                        Utils.convertUnixToDate(bookingObj.getString("ModifiedOn")),
                                        bookingObj.getString("Hash")
                                );
                                DBBookingLocal.addBooking(booking, db);
                            }
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

    public static void getRoomsINeed(String userId, Context context, SQLiteDatabase db, DbManager dbManager) {
        String url = apiUrl + "GetRoomsUserNeeds?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            db.execSQL("DELETE FROM " + dbManager.ROOM_TABLE);

                            JSONArray roomList = new JSONArray(obj.getString("RoomList"));

                            for (int i = 0; i < roomList.length(); i++) {
                                JSONObject roomObj = roomList.getJSONObject(i);
                                Room room = new Room(roomObj.getInt("Id"), roomObj.getString("Name"), roomObj.getString("Description"));
                                DBRoomLocal.addRoom(room, context, db);
                            }
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
                                    null,
                                    Utils.convertUnixToDate(obj.getString("ModifiedOn")),
                                    obj.getString("Hash")
                            );
                            DBBookingLocal.addBooking(booking, db);

                            callback.onSuccess("Validation successful");
                        } else {
                            callback.onError(obj.getString("Message"));
                        }
                    } catch (JSONException e) {
                        callback.onError(e.getMessage());
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

}
