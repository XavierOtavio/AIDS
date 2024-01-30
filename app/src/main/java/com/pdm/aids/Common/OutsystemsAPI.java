package com.pdm.aids.Common;

import android.content.ContentValues;
import android.content.Context;
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
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Room.RoomImage;
import com.pdm.aids.Ticket.Ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        //Get room images
        getRoomImages(userId, context, db, dbManager);
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

    public static void getRoomImages(String roomId, Context context, SQLiteDatabase db, DbManager dbManager) {
        String url = apiUrl + "GetRoomImagesUserNeeds?UserId=" + roomId;

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("HTTPCode").equals("200")) {
                            db.execSQL("DELETE FROM " + dbManager.ROOM_IMAGE_TABLE);
                            JSONArray imageList = new JSONArray(obj.getString("RoomImageList"));
                            for (int i = 0; i < imageList.length(); i++) {
                                JSONObject imageObj = imageList.getJSONObject(i);

                                String folderPath = context.getFilesDir() + "/RoomImages";
                                File folder = new File(folderPath);
                                if (!folder.exists()) {
                                    if (!folder.mkdirs()) {
                                        new Exception("Failed to create folder");
                                    }
                                }
                                String filePath = folderPath + "/" + imageObj.getString("Filename");
                                File imageFile = new File(filePath);
                                if (!imageFile.exists()) {
                                    String base64Image = imageObj.getString("Image");
                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    FileOutputStream stream = new FileOutputStream(filePath);
                                    Bitmap.CompressFormat extension = Bitmap.CompressFormat.valueOf(imageObj.getString("Filename").split("\\.")[1].toUpperCase(Locale.getDefault()).replace("JPG", "JPEG"));
                                    bmp.compress(extension, 100, stream);
                                    stream.close();
                                    bmp.recycle();
                                }

                                RoomImage roomImage = new RoomImage();
                                roomImage.setFileName(imageObj.getString("Filename"));
                                roomImage.setImagePath(filePath);
                                roomImage.setRoomId(Integer.parseInt(imageObj.getString("RoomId")));

                                DBRoomImageLocal.addRoomImage(roomImage, db);
                            }
                        } else {
                            Toast.makeText(context, obj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
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

    public static void submitTicket(Ticket ticket, Context context, VolleyCallback callback) {
        String url = apiUrl + "SubmitTicket";

        String ticketJson = ticket.toString();

        try {
            JSONObject ticketJsonObject = new JSONObject(ticketJson);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, ticketJsonObject,
                    response -> {
                        try {
                            if (response.getString("HTTPCode").equals("200")) {
                                callback.onSuccess(response.getString("Message"));
                            } else {
                                callback.onError(response.getString("Message"));
                            }
                        } catch (JSONException e) {
                            callback.onError("Error parsing response");
                        }
                    },
                    error -> callback.onError("Error submitting ticket: " + error.getMessage()));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            callback.onError("Error converting Ticket to JSON");
        }
    }

}
