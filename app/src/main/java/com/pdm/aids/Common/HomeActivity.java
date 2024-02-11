package com.pdm.aids.Common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.journeyapps.barcodescanner.ScanOptions;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingDetails.BookingDetailActivity;
import com.pdm.aids.Booking.BookingList.BookingListActivity;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Ticket.TicketList.TicketListActivity;
import com.pdm.aids.databinding.ActivityHomeBinding;
public class HomeActivity extends AppCompatActivity {

    private Button btnBookingList, btnTicketList, btnWeb, btnActiveBooking;
    private ImageButton btnShowQrCode, bntUser;
    private ImageButton btnLogout;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-----------------View Binding-----------------
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //-----------------Initialize Variables-----------------
        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        btnBookingList = (Button) findViewById(R.id.button_goToBookingList);
        btnTicketList = (Button) findViewById(R.id.button_report);
        btnShowQrCode = findViewById(R.id.button_showQrCode);
        btnWeb = (Button) findViewById(R.id.button_web);
        btnActiveBooking = findViewById(R.id.button_activeBooking);
        btnLogout = findViewById(R.id.button_logout);
        binding.username.setText(sharedpreferences.getString("Name", ""));

        //-----------------Listeners-----------------
        btnBookingList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingListActivity.class);
            startActivity(intent);
        });

        btnTicketList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TicketListActivity.class);
            startActivity(intent);
        });

        btnWeb.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://personal-8o07igno.outsystemscloud.com/AIDS/Bookings"));
            startActivity(browserIntent);
        });

        btnShowQrCode.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://personal-8o07igno.outsystemscloud.com/AIDS/Bookings"));
            startActivity(browserIntent);
        });

        btnActiveBooking.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingListActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            logout();
        });

        binding.userIcon.setOnClickListener(v -> {
            if (binding.buttonLogout.getVisibility() == View.VISIBLE) {
                binding.buttonLogout.setVisibility(View.GONE);
            } else {
                binding.buttonLogout.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonReadQrCode.setOnClickListener(view -> checkPermissionAndShowActivity(this));
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
            String bookingHash = new DBBookingLocal().getCurrentAvaliableBooking(dbHelper.getWritableDatabase()).getHash();
            Toast.makeText(this, "Scanned: " + bookingHash, Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("Id", "");

            OutsystemsAPI.validateEntry(bookingHash, id, result.getContents(), this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(HomeActivity.this, result, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, BookingDetailActivity.class);
                    intent.putExtra("bookingHash", bookingHash);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(HomeActivity.this, "Validation failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

}