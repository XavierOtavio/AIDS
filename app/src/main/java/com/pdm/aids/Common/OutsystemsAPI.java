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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                } , error -> {
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
                } , error -> {
                callback.onError(error.getMessage());
            }
        );
        queue.add(stringRequest);
    }

    public static void getDataFromAPI(String userId, Context context) {
        //TODO: Need to implement this. The client must be able to get the user data from the API and stored it in the local database.

            //Get bookings by user
            getBookingsByUser(userId, context);
            //Get rooms
            getRoomsINeed(userId, context);

    }

    public static void getBookingsByUser(String userId, Context context) {
        String url = apiUrl + "GetAllBookingsByUser?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            DBBookingLocal dbBookingLocal = new DBBookingLocal(context);
                            SQLiteDatabase db = dbBookingLocal.getWritableDatabase();
                            db.execSQL("DELETE FROM " + dbBookingLocal.BOOKING_TABLE);

                            JSONArray bookingList = new JSONArray(obj.getString("BookingList"));

                            for (int i = 0; i < bookingList.length(); i++) {
                                JSONObject bookingObj = bookingList.getJSONObject(i);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                Booking booking = new Booking(
                                        bookingObj.getInt("RoomId"),
                                        bookingObj.getInt("ReservedBy"),
                                        bookingObj.getInt("BookingStatusId"),
                                        dateFormat.parse(bookingObj.getString("ExpectedStartDate")),
                                        dateFormat.parse(bookingObj.getString("ExpectedEndDate")),
                                        dateFormat.parse(bookingObj.getString("ActualStartDate")),
                                        dateFormat.parse(bookingObj.getString("ActualEndDate")),
                                        dateFormat.parse(bookingObj.getString("ModifiedOn")),
                                        bookingObj.getString("Hash")
                                );

                                DBBookingLocal.addBooking(booking, context);
                            }
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

    public static void getRoomsINeed(String userId, Context context) {
        String url = apiUrl + "GetRoomsUserNeeds?UserId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            DBRoomLocal dbRoomLocal = new DBRoomLocal(context);
                            SQLiteDatabase db = dbRoomLocal.getWritableDatabase();
                            db.execSQL("DELETE FROM " + dbRoomLocal.ROOM_TABLE);

                            JSONArray roomList = new JSONArray(obj.getString("RoomList"));

                            for (int i = 0; i < roomList.length(); i++) {
                                JSONObject room = roomList.getJSONObject(i);
                                ContentValues cv = new ContentValues();
                                cv.put(dbRoomLocal.COLUMN_ID, room.getInt("Id"));
                                cv.put(dbRoomLocal.COLUMN_NAME, room.getString("Name"));
                                cv.put(dbRoomLocal.COLUMN_DESCRIPTION, room.getString("Description"));
                                db.insert(dbRoomLocal.ROOM_TABLE, null, cv);
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
}
