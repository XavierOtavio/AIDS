package com.pdm.aids.Booking.BookingHistory;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingDetails.BookingDetailActivity;
import com.pdm.aids.Booking.BookingHistory.BookingHistoryAdapter;
import com.pdm.aids.Booking.BookingHistory.ListData;
import com.pdm.aids.Booking.BookingList.BookingListActivity;
import com.pdm.aids.Booking.BookingList.BookingListAdapter;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.HomeActivity;
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
import java.util.Set;

public class BookingHistoryActivity extends AppCompatActivity {
    ActivityBookingHistoryBinding binding;
    BookingHistoryAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    private String id;
    private NetworkChecker networkChecker;
    public static Booking selectedBooking;
    public static Room selectedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        id = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE)
                .getString("Id", "");

        OutsystemsAPI.getBookingsHistory(id, this, bookingArrayList -> {
            bookings.clear();
            bookings.addAll(bookingArrayList);
            final int[] pendingTasks = {bookings.stream().mapToInt(Booking::getRoomId).distinct().toArray().length};
            for (int i = 0; i < bookings.size(); i++) {
                int tempRoomId = bookings.get(i).getRoomId();
                if (rooms.stream().noneMatch(room -> room.getId() == tempRoomId)) {
                    OutsystemsAPI.getRoomById(tempRoomId, this, new OutsystemsAPI.RoomCallback() {
                        @Override
                        public void onRoomsReceived(Room room) {
                            if(rooms.stream().noneMatch(r -> r.getId() == room.getId())) {
                                rooms.add(room);
                                pendingTasks[0]--;
                                updateUIWithBookings(pendingTasks);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            throw new RuntimeException("Error getting room: " + error);
                        }
                    });
                }
            }
            if(pendingTasks[0] == 0) {
                updateUIWithBookings(pendingTasks);
            }
        });

        TextView textTitle_toolbar = findViewById(R.id.toolbar_booking_history_title);
        ListView listItem = findViewById(R.id.listView);

        textTitle_toolbar.setText("Reservas");
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

        binding.buttonUpcoming.setOnClickListener(v -> finish());

        listItem.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedBooking = bookings.get(i);
            for (int j = 0; j < rooms.size(); j++) {
                if (rooms.get(j).getId() == selectedBooking.getRoomId()) {
                    selectedRoom = rooms.get(j);
                }
            }
            Intent intent = new Intent(BookingHistoryActivity.this, BookingDetailActivity.class);
            startActivity(intent);
        });

        binding.buttonGoToHome.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(BookingHistoryActivity.this, HomeActivity.class));
        });

        binding.buttonReadQrCode.setOnClickListener(view -> checkPermissionAndShowActivity(this));
    }

    private void updateUIWithBookings(int[] pendingTasks) {
        if (pendingTasks[0] == 0) {
            for (int i = 0; i < bookings.size(); i++) {
                int tempRoomId = bookings.get(i).getRoomId();
                dataArrayList.add(new ListData(
                        rooms.stream().filter(room -> room.getId() == tempRoomId).findFirst().get().getName(),
                        bookings.get(i).getExpectedStartDate(),
                        bookings.get(i).getExpectedEndDate(),
                        bookings.get(i).getBookingStatusId()));
            }
            if (dataArrayList.size() == 0) {
                binding.emptyBookingList.setVisibility(View.VISIBLE);
                binding.listView.setVisibility(View.GONE);
            } else {
                listAdapter = new BookingHistoryAdapter(this, dataArrayList, this);
                binding.listView.setAdapter(listAdapter);
                binding.listView.setClickable(true);
            }
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

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            DbManager dbHelper = new DbManager(BookingHistoryActivity.this);
            String bookingHash = new DBBookingLocal().getCurrentAvaliableBooking(dbHelper.getWritableDatabase()).getHash();
            Toast.makeText(this, "Scanned: " + bookingHash, Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("Id", "");

            OutsystemsAPI.validateEntry(bookingHash, id, result.getContents(), this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(BookingHistoryActivity.this, result, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BookingHistoryActivity.this, BookingDetailActivity.class);
                    intent.putExtra("bookingHash", bookingHash);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(BookingHistoryActivity.this, "Validation failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
}