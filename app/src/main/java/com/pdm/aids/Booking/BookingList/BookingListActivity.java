package com.pdm.aids.Booking.BookingList;

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
import com.pdm.aids.databinding.ActivityBookingListBinding;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

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
    private NetworkChecker networkChecker;
    private ExecutorService executorService;
    private Handler uiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-----------------View Binding-----------------
        binding = ActivityBookingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----------------Toolbar-----------------
        binding.toolbarBookingList.setNavigationOnClickListener(v -> finish());
        binding.toolbarBookingTitle.setText("Reservas");

        //-----------------Lazy Loading-----------------
        executorService = Executors.newSingleThreadExecutor();
        uiHandler = new Handler(Looper.getMainLooper());
        binding.progressBar.setVisibility(View.VISIBLE);
        loadDataInBackGround();

        //-----------------Internet Connection-----------------
        networkChecker = new NetworkChecker(this);
        LinearLayout internetConnectionWarning = binding.internetConnectionWarning;

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

        //-----------------Item Click Listener-----------------
        binding.listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(BookingListActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingHash", bookings.get(i).getHash());
            startActivity(intent);
        });
    }

    private void loadDataInBackGround() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try (DbManager dataBaseHelper = new DbManager(BookingListActivity.this)) {
                    String id = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE).getString("Id", "");

                    if(networkChecker.isInternetConnected()) {
                        OutsystemsAPI.getDataFromAPI(id, BookingListActivity.this);
                    }

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
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void  updateList() {
        listAdapter = new BookingListAdapter( BookingListActivity.this, dataArrayList, BookingListActivity.this);
        binding.listView.setAdapter(listAdapter);
        binding.listView.setClickable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    //TODO: review this method
    //TODO: Is this needed anymore?
//    private View.OnClickListener updateList() {
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
//        return null;
//    }
}