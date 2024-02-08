package com.pdm.aids.Booking.BookingHistory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingDetails.BookingDetailActivity;
import com.pdm.aids.Booking.BookingHistory.BookingHistoryAdapter;
import com.pdm.aids.Booking.BookingHistory.ListData;
import com.pdm.aids.Booking.BookingList.BookingListActivity;
import com.pdm.aids.Booking.BookingList.BookingListAdapter;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.NetworkChecker;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Room.RoomImage;
import com.pdm.aids.databinding.ActivityBookingHistoryBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    ActivityBookingHistoryBinding binding;
    BookingHistoryAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    Room currentRoom;
    Bitmap currentRoomImage;
    ListData listData;
    private String id;
    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        binding = ActivityBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        id = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE)
                .getString("Id", "");

        OutsystemsAPI.getBookingsHistory(id, this, bookingArrayList -> {
            bookings = bookingArrayList;
            updateUIWithBookings();
        });


        TextView textTitle_toolbar = findViewById(R.id.toolbar_booking_history_title);
        Toolbar toolbar = findViewById(R.id.toolbar_booking_history_list);
        ListView listItem = findViewById(R.id.listView);

        textTitle_toolbar.setText("Histórico");
        toolbar.setNavigationOnClickListener(v -> finish());
        networkChecker = new NetworkChecker(this);

        LinearLayout internetConnectionWarning = findViewById(R.id.internetConnectionWarning);

        if (networkChecker.isInternetConnected()) {
            internetConnectionWarning.setVisibility(LinearLayout.GONE);
        } else {
            internetConnectionWarning.setVisibility(LinearLayout.VISIBLE);
        }

        networkChecker.setNetworkCallbackListener(new NetworkChecker.NetworkCallbackListener() {
            @Override
            public void onNetworkAvailable() {
                runOnUiThread(() -> {
                    internetConnectionWarning.setVisibility(LinearLayout.GONE);
                });
            }

            @Override
            public void onNetworkUnavailable() {
                runOnUiThread(() -> {
                    internetConnectionWarning.setVisibility(LinearLayout.VISIBLE);
                });
            }
        });

        listItem.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(BookingHistoryActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingHash", bookings.get(i).getHash());
            startActivity(intent);
        });
    }

    private void updateUIWithBookings() {
        try (DbManager dataBaseHelper = new DbManager(this)) {
            OutsystemsAPI.getDataFromAPI(id, this, new OutsystemsAPI.DataLoadCallback() {
                @Override
                public void onDataLoaded() {
                    rooms = new DBRoomLocal().getAllRooms(dataBaseHelper.getWritableDatabase());

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
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), "Failed to load data: " + error, Toast.LENGTH_SHORT).show();
                }
            });

            if (dataArrayList.size() == 0) {
                binding.emptyBookingList.setVisibility(View.VISIBLE);
                binding.listView.setVisibility(View.GONE);
            } else {
                listAdapter = new BookingHistoryAdapter(this, dataArrayList, this);
                binding.listView.setAdapter(listAdapter);
                binding.listView.setClickable(true);
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        networkChecker.registerNetworkCallback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        networkChecker.unregisterNetworkCallback();
    }
}