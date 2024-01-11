package com.pdm.aids.Booking.BookingList;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    ActivityTicketListBinding binding;
    BookingListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();

    List<Booking> bookings = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    Room currentRoom;
    ListData listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view of the binding object

        TextView textTitle_toolbar = findViewById(R.id.toolbar_title);
        textTitle_toolbar.setText("Reservas");

        try (DbManager dataBaseHelper = new DbManager(this)) {
            rooms = new DBRoomLocal().getAllRooms(dataBaseHelper.getWritableDatabase());

            bookings = new DBBookingLocal().getAllBookings(dataBaseHelper.getWritableDatabase());

            for (int i = 0; i < bookings.size(); i++) {
                for (int j = 0; j < rooms.size(); j++) {
                    if (rooms.get(j).getId() == bookings.get(i).getRoomId()) {
                        currentRoom = rooms.get(j);
                        System.out.println(currentRoom.getName());
                    }
                }
                listData = new ListData(currentRoom.getName(), bookings.get(i).getExpectedStartDate(), bookings.get(i).getExpectedEndDate());
                dataArrayList.add(listData);
            }

            listAdapter = new BookingListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error accessing database", Toast.LENGTH_SHORT).show();
        }
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