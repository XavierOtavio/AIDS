package com.pdm.aids.Booking.BookingDetails;

import static java.text.DateFormat.getDateTimeInstance;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.NetworkChecker;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.Common.Utils;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.databinding.ActivityBookingDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {
    private ActivityBookingDetailBinding binding;
    private Booking booking;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "PT"));
    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();

        try (DbManager dbManager = new DbManager(this)) {
            TextView textTitle_toolbar = findViewById(R.id.toolbar_title);
            TextView qrCodeLabel = findViewById(R.id.qrCodeLabel);
            TextView startDate = findViewById(R.id.startDate);
            TextView endDate = findViewById(R.id.endDate);
            TextView enterRoomDate = findViewById(R.id.enterRoomDate);
            TextView exitRoomDate = findViewById(R.id.exitRoomDate);
            TextView roomName = findViewById(R.id.roomName);
            ImageView noWifiImage = findViewById(R.id.noWifiImage);
            ImageView imageViewCaptured = findViewById(R.id.image_view_captured);
            ImageView roomImage = findViewById(R.id.roomImage);

            networkChecker = new NetworkChecker(this);

            booking = DBBookingLocal.getBookingByHash(getIntent().getStringExtra("bookingHash"), dbManager.getWritableDatabase());
            Room room = DBRoomLocal.getRoomById(booking.getRoomId(), dbManager.getReadableDatabase());

            textTitle_toolbar.setText(String.format("Reserva: %s", room != null ? room.getName() : ""));
            startDate.setText(Utils.isDateNull(booking.getExpectedStartDate()) ? "-" : dateFormat.format(booking.getExpectedStartDate()));
            endDate.setText(Utils.isDateNull(booking.getExpectedEndDate()) ? "-" : dateFormat.format(booking.getExpectedEndDate()));
            enterRoomDate.setText(Utils.isDateNull(booking.getActualStartDate()) ? "-" : dateFormat.format(booking.getActualStartDate()));
            exitRoomDate.setText(Utils.isDateNull(booking.getActualEndDate()) ? "-" : dateFormat.format(booking.getActualEndDate()));
            if (room != null) {
                roomImage.setImageBitmap(DBRoomImageLocal.getRoomImageByRoomId(room.getId(), dbManager.getReadableDatabase()));
                roomName.setText(room.getName());
            }

            if (!enterRoomDate.getText().equals("-")) {
                Intent intent = getIntent();
                String hash = intent.getStringExtra("bookingHash");

                binding.imageViewCaptured.setVisibility(View.GONE);
                binding.noWifiImage.setVisibility(View.GONE);
                binding.QRimage.setVisibility(View.VISIBLE);
                qrCodeLabel.setText(R.string.read_qrcode_exit);

                Utils u = new Utils();
                Bitmap qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
                binding.QRimage.setImageBitmap(qrBitmap);
            } else {

                if (networkChecker.isInternetConnected()) {
                    noWifiImage.setVisibility(View.GONE);
                    imageViewCaptured.setVisibility(View.VISIBLE);
                    qrCodeLabel.setText(R.string.read_qr);
                } else {
                    noWifiImage.setVisibility(View.VISIBLE);
                    imageViewCaptured.setVisibility(View.GONE);
                    qrCodeLabel.setText(R.string.qr_failed);
                }

                networkChecker.setNetworkCallbackListener(new NetworkChecker.NetworkCallbackListener() {
                    @Override
                    public void onNetworkAvailable() {
                        runOnUiThread(() -> {
                            noWifiImage.setVisibility(View.GONE);
                            imageViewCaptured.setVisibility(View.VISIBLE);
                            qrCodeLabel.setText(R.string.read_qr);
                        });
                    }

                    @Override
                    public void onNetworkUnavailable() {
                        runOnUiThread(() -> {
                            noWifiImage.setVisibility(View.VISIBLE);
                            imageViewCaptured.setVisibility(View.GONE);
                            qrCodeLabel.setText(R.string.qr_failed);
                        });
                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Registra o NetworkCallback
        networkChecker.registerNetworkCallback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Desregistra o NetworkCallback
        networkChecker.unregisterNetworkCallback();
    }

    @Override
    protected void onResume() {
        TextView qrCodeLabel = findViewById(R.id.qrCodeLabel);
        TextView enterRoomDate = findViewById(R.id.enterRoomDate);

        enterRoomDate.setText(Utils.isDateNull(booking.getActualStartDate()) ? "-" : dateFormat.format(booking.getActualStartDate()));

        if (!enterRoomDate.getText().equals("-")) {
            Intent intent = getIntent();
            String hash = intent.getStringExtra("bookingHash");

            binding.imageViewCaptured.setVisibility(View.GONE);
            binding.QRimage.setVisibility(View.VISIBLE);
            qrCodeLabel.setText(R.string.read_qrcode_exit);

            Utils u = new Utils();
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
            binding.QRimage.setImageBitmap(qrBitmap);
        }
        super.onResume();
    }


    private void initBinding() {
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initViews() {
        binding.imageViewCaptured.setOnClickListener(view -> checkPermissionAndShowActivity(this));
        binding.toolbarMain.setNavigationOnClickListener(v -> finish());
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
            Intent intent = getIntent();
            String hash = intent.getStringExtra("bookingHash");

            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("Id", "");

            OutsystemsAPI.validateEntry(hash, id, result.getContents(), this, new OutsystemsAPI.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(BookingDetailActivity.this, result, Toast.LENGTH_SHORT).show();
                    recreate();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(BookingDetailActivity.this, "Validation failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    });


}
