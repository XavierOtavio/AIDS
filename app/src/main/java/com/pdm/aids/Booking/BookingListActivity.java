package com.pdm.aids.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Ticket.CreateTicketActivity;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    ActivityTicketListBinding binding;
    BookingListAdapter listAdapter;
    ArrayList<com.pdm.aids.Booking.ListData> dataArrayList = new ArrayList<>();

    List<Booking> bookings = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    Room currentRoom;
    com.pdm.aids.Booking.ListData listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view of the binding object

        TextView textTitle_toolbar = findViewById(R.id.toolbar_title);
        textTitle_toolbar.setText("Reservas");

        ImageView add = findViewById(R.id.toolbar_add);
        /*add.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
            startActivity(intent);
        });*/
//        try (DBRoomLocal dataBaseHelper = new DBRoomLocal(this)) {
//            rooms = dataBaseHelper.getAllRooms();
//            Toast.makeText(getApplicationContext(), "Rooms: " + rooms.size(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Error reading rooms", Toast.LENGTH_SHORT).show();
//        }
//
//        try (DBBookingLocal dataBaseHelper = new DBBookingLocal(this)) {
//            bookings = dataBaseHelper.getAllBookings();
//            Toast.makeText(getApplicationContext(), "Bookings: " + bookings.size(), Toast.LENGTH_SHORT).show();
//            for (int i = 0; i < bookings.size(); i++) {
//                for (int j = 0; j < rooms.size(); j++) {
//                    if (rooms.get(j).getId() == bookings.get(j).getRoomId()) {
//                        currentRoom = rooms.get(j);
//                        System.out.println(currentRoom.getName());
//                    }
//                }
//                listData = new com.pdm.aids.Booking.ListData(currentRoom.getName(), bookings.get(i).getExpectedStartDate(), bookings.get(i).getExpectedEndDate());
//                dataArrayList.add(listData);
//            }
//
//            listAdapter = new BookingListAdapter(this, dataArrayList, this);
//            binding.listView.setAdapter(listAdapter);
//            binding.listView.setClickable(true);
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Error reading bookings", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    //TODO: review this method
    private void updateList() {
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
    }
}