package com.pdm.aids.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pdm.aids.R;
import com.pdm.aids.Tickets.CreateTicketActivity;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;
import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    ActivityTicketListBinding binding;
    BookingListAdapter listAdapter;
    ArrayList<com.pdm.aids.booking.ListData> dataArrayList = new ArrayList<>();

    List<Booking> bookings = new ArrayList<>();
    com.pdm.aids.booking.ListData listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view of the binding object

        TextView textTitle_toolbar = findViewById(R.id.toolbar_title);
        textTitle_toolbar.setText("Reservas");

        ImageView add = findViewById(R.id.toolbar_add);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
            startActivity(intent);
        });

        try (DBBookingLocal dataBaseHelper = new DBBookingLocal(this)) {
            bookings = dataBaseHelper.getAllBookings();

            for (int i = 0; i < bookings.size(); i++) {
                listData = new com.pdm.aids.booking.ListData(bookings.get(i).getId(), bookings.get(i).getRoomId(), bookings.get(i).getQrImage(bookings.get(i).getHash()));
                dataArrayList.add(listData);
            }


            listAdapter = new BookingListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    private void updateList() {
        try (DBBookingLocal dataBaseHelper = new DBBookingLocal(this)) {
            bookings = dataBaseHelper.getAllBookings();
            dataArrayList.clear();
            for (int i = 0; i < bookings.size(); i++) {
                listData = new ListData(bookings.get(i).getId(), bookings.get(i).getRoomId(), bookings.get(i).getQrImage(bookings.get(i).getHash()));
                dataArrayList.add(listData);
            }

            listAdapter = new BookingListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}