package com.pdm.aids.Common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingDetails.BookingDetailActivity;
import com.pdm.aids.Booking.BookingList.BookingListActivity;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Ticket.TicketDetails.CreateTicketActivity;
import com.pdm.aids.databinding.ActivityHomeBinding;

import java.text.SimpleDateFormat;

public class HomeActivity extends AppCompatActivity {

    private Button btnBookingList, btnReport, btnWeb, btnActiveBooking;
    private ImageButton btnShowQrCode, bntUser;
    private ImageButton btnLogout;
    private ActivityHomeBinding binding;
    private DbManager dbHelper;
    private Booking booking;
    private Room room;
    private Utils u;
    private Bitmap qrBitmap;
    SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-----------------View Binding-----------------
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----------------Initialize Variables-----------------
        u = new Utils();
        dbHelper = new DbManager(this);
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        btnBookingList = (Button) findViewById(R.id.button_goToBookingList);
        btnReport = (Button) findViewById(R.id.button_report);
        btnShowQrCode = findViewById(R.id.button_showQrCode);
        btnWeb = (Button) findViewById(R.id.button_web);
        btnActiveBooking = findViewById(R.id.button_activeBooking);
        btnLogout = findViewById(R.id.button_logout);

        binding.username.setText(sharedpreferences.getString("Name", ""));

//        booking = new DBBookingLocal().getCurrentOnGoingBooking(dbHelper.getWritableDatabase());
//        if (booking != null) {
//            String hash = booking.getHash();
//            int roomId = booking.getRoomId();
//            room = new DBRoomLocal().getRoomById(roomId, dbHelper.getWritableDatabase());
//
//            binding.roomTitle.setText(room.getName());
//            binding.expectedStartDate.setText(Utils.isDateNull(booking.getExpectedStartDate()) ? "-" : dateFormatHour.format(booking.getExpectedStartDate()) + "\n" + dateFormatDay.format(booking.getExpectedStartDate()));
//            binding.expectedEndDate.setText(Utils.isDateNull(booking.getExpectedEndDate()) ? "-" : dateFormatHour.format(booking.getExpectedEndDate()) + "\n" + dateFormatDay.format(booking.getExpectedEndDate()));
//
//            qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
//            binding.buttonShowQrCode.setImageBitmap(qrBitmap);
//
//            binding.bookingStatus.setText("Ativa");
//            binding.bookingStatus.setTextColor(getResources().getColor(R.color.green));
//            binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.green));
//
//            btnActiveBooking.setVisibility(View.VISIBLE);
//        } else {
//            binding.activeBooking.setVisibility(View.GONE);
//        }

        //-----------------Listeners-----------------
        //Booking List
        btnBookingList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingListActivity.class);
            startActivity(intent);
        });

        //Report Problem
        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateTicketActivity.class);
            intent.putExtra("bookingHash", booking.getHash());
            startActivity(intent);
        });

        //Go to Outsystems Web
        btnWeb.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://personal-8o07igno.outsystemscloud.com/AIDS/Bookings"));
            startActivity(browserIntent);
        });

        //Enter in the active booking details
        btnActiveBooking.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingDetailActivity.class);
            intent.putExtra("bookingHash", booking.getHash());
            intent.putExtra("bookingId", booking.getId());
            startActivity(intent);
        });

        //Show QR Code
        binding.buttonShowQrCode.setOnClickListener(view -> u.showImageDialog(HomeActivity.this, qrBitmap));

        //Logout
        btnLogout.setOnClickListener(v -> {
            logout();
        });

        //Show user options
        binding.userIcon.setOnClickListener(v -> {
            if (binding.buttonLogout.getVisibility() == View.VISIBLE) {
                binding.buttonLogout.setVisibility(View.GONE);
            } else {
                binding.buttonLogout.setVisibility(View.VISIBLE);
            }
        });

        //Capture QR Code
        binding.buttonReadQrCode.setOnClickListener(view -> checkPermissionAndShowActivity(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        booking = new DBBookingLocal().getCurrentOnGoingBooking(dbHelper.getWritableDatabase());
        if (booking != null) {
            String hash = booking.getHash();
            int roomId = booking.getRoomId();
            room = new DBRoomLocal().getRoomById(roomId, dbHelper.getWritableDatabase());

            binding.roomTitle.setText(room.getName());
            binding.expectedStartDate.setText(Utils.isDateNull(booking.getExpectedStartDate()) ? "-" : dateFormatHour.format(booking.getExpectedStartDate()) + "\n" + dateFormatDay.format(booking.getExpectedStartDate()));
            binding.expectedEndDate.setText(Utils.isDateNull(booking.getExpectedEndDate()) ? "-" : dateFormatHour.format(booking.getExpectedEndDate()) + "\n" + dateFormatDay.format(booking.getExpectedEndDate()));

            qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
            binding.buttonShowQrCode.setImageBitmap(qrBitmap);

            binding.bookingStatus.setText("Ativa");
            binding.bookingStatus.setTextColor(getResources().getColor(R.color.green));
            binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.green));

            btnActiveBooking.setVisibility(View.VISIBLE);
        } else {
            binding.activeBooking.setVisibility(View.GONE);
        }
    }

    private void logout() {
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();

        finish();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
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
            DbManager dbHelper = new DbManager(HomeActivity.this);
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("Id", "");
            Booking b = new DBBookingLocal().getCurrentAvaliableBooking(dbHelper.getWritableDatabase());
            if (b == null) {
                OutsystemsAPI.RefreshBookings(id, this, new OutsystemsAPI.DataLoadCallback() {
                    @Override
                    public void onDataLoaded() {
                        Booking b = new DBBookingLocal().getCurrentAvaliableBooking(dbHelper.getWritableDatabase());
                        String bookingHash = b.getHash();
                        validate(bookingHash, id, result);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(HomeActivity.this, "Booking not found: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                String bookingHash = b.getHash();
                validate(bookingHash, id, result);
            }
        }
    });

    private void validate(String bookingHash, String id, ScanIntentResult result) {
        OutsystemsAPI.validateEntry(bookingHash, id, result.getContents(), HomeActivity.this, new OutsystemsAPI.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, BookingDetailActivity.class);
                booking = new DBBookingLocal().getCurrentOnGoingBooking(dbHelper.getWritableDatabase());
                intent.putExtra("bookingHash", bookingHash);
                intent.putExtra("bookingId", booking.getId());
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(HomeActivity.this, "Validation failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}