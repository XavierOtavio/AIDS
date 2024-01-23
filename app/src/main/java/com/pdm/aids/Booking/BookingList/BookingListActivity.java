package com.pdm.aids.Booking.BookingList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingDetails.BookingDetailActivity;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Room.RoomImage;
import com.pdm.aids.databinding.ActivityBookingListBinding;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    ActivityBookingListBinding binding;
    BookingListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();

    List<Booking> bookings = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    List<RoomImage> roomsImage = new ArrayList<>();
    Room currentRoom;
    Bitmap currentRoomImage;
    ListData listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        binding = ActivityBookingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView textTitle_toolbar = findViewById(R.id.toolbar_booking_title);
        Toolbar toolbar = findViewById(R.id.toolbar_booking_list);
        ListView listItem = findViewById(R.id.listView);

        textTitle_toolbar.setText("Reservas");
        toolbar.setNavigationOnClickListener(v -> finish());


        listItem.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(BookingListActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingHash", bookings.get(i).getHash());
            startActivity(intent);
        });


        try (DbManager dataBaseHelper = new DbManager(this)) {
            String id = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE)
                    .getString("Id", "");

            OutsystemsAPI.getDataFromAPI(id, this);

            rooms = new DBRoomLocal().getAllRooms(dataBaseHelper.getWritableDatabase());
            bookings = new DBBookingLocal().getAllBookings(dataBaseHelper.getWritableDatabase());

            for (int i = 0; i < bookings.size(); i++) {
                for (int j = 0; j < rooms.size(); j++) {
                    if (rooms.get(j).getId() == bookings.get(i).getRoomId()) {
                        currentRoom = rooms.get(j);
                        currentRoomImage = new DBRoomImageLocal().getRoomImageByRoomId(currentRoom.getId(), dataBaseHelper.getWritableDatabase());
                    }
                }
                listData = new ListData(currentRoom.getName(),
                        bookings.get(i).getExpectedStartDate(),
                        bookings.get(i).getExpectedEndDate(),
                        currentRoomImage);
                dataArrayList.add(listData);
            }

            listAdapter = new BookingListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    //TODO: review this method
    private View.OnClickListener updateList() {
//        try (DBBookingLocal dataBaseHelper = new DBBookingLocal(this)) {
//            bookings = dataBaseHelper.getAllBookings();
//            dataArrayList.clear();
//            for (int i = 0; i < bookings.size(); i++) {
//                listData = new ListData(bookings.get(i).getRoomId(), bookings.get(i).getExpectedStartDate(), bookings.get(i).getExpectedEndDate());
//                dataArrayList.add(listData);
//            }
//
//            listAdapter = new BookingListAdapter(this, dataArrayList, this);
//            binding.listView.setAdapter(listAdapter);
//            binding.listView.setClickable(true);
//        } catch (Exception e) {
//            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
//            toast.show();
//        }
        return null;
    }
}