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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




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
                                    JSONObject booking = bookingList.getJSONObject(i);
                                    ContentValues cv = new ContentValues();
                                    cv.put(dbBookingLocal.COLUMN_ID, booking.getInt("Id"));
                                    cv.put(dbBookingLocal.COLUMN_ROOM_ID, booking.getInt("RoomId"));
                                    cv.put(dbBookingLocal.COLUMN_USER_ID, booking.getInt("ReservedBy"));
                                    cv.put(dbBookingLocal.COLUMN_BOOKING_STATUS_ID, 0); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    cv.put(dbBookingLocal.COLUMN_EXPECTED_START_DATE, booking.getString("ExpectedStartDate"));
                                    cv.put(dbBookingLocal.COLUMN_EXPECTED_END_DATE, booking.getString("ExpectedEndDate"));
                                    cv.put(dbBookingLocal.COLUMN_ACTUAL_START_DATE, ""); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    cv.put(dbBookingLocal.COLUMN_ACTUAL_END_DATE, ""); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    cv.put(dbBookingLocal.COLUMN_DATE_TIME, ""); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    cv.put(dbBookingLocal.COLUMN_HASH, ""); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    cv.put(dbBookingLocal.COLUMN_QR_CODE, ""); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                    db.insert(dbBookingLocal.BOOKING_TABLE, null, cv);
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
