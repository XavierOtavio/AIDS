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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.HomeActivity;
import com.pdm.aids.Common.NetworkChecker;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.Common.Utils;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomImageLocal;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketDetails.CreateTicketActivity;
import com.pdm.aids.Ticket.TicketList.ListAdapter;
import com.pdm.aids.Ticket.TicketList.ListData;
import com.pdm.aids.databinding.ActivityBookingDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingDetailActivity extends AppCompatActivity {
    private ActivityBookingDetailBinding binding;
    private Booking booking;
    private Room room;
    private Bitmap qrBitmap, roomImageBitmap;
    private Utils utils;
    ListData listData;
    ArrayList<ListData> ticketLisDataArray = new ArrayList<>();
    ArrayList<Ticket> tickets = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "PT"));
    SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH:mm", new Locale("pt", "PT"));
    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "PT"));
    private NetworkChecker networkChecker;
    private ExecutorService executorService;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-----------------View Binding-----------------
        initBinding();
        initViews();
        //-----------------Lazy Loading-----------------
        Utils utils = new Utils();
        executorService = Executors.newSingleThreadExecutor();
        uiHandler = new Handler(Looper.getMainLooper());
        binding.progressBar.setVisibility(View.VISIBLE);
//        binding.linearLayoutTop.setVisibility(View.GONE);
        binding.linearLayoutContent.setVisibility(View.GONE);
        setupNetworkChecker();
        loadDataInBackGround();

        binding.buttonReport.setOnClickListener(v -> {
            Intent intent = new Intent(BookingDetailActivity.this, CreateTicketActivity.class);
            intent.putExtra("bookingHash", booking.getHash());
            startActivity(intent);
        });

        binding.toolbarMain.setNavigationOnClickListener(v -> finish());

        binding.buttonQr.setOnClickListener(view -> utils.showImageDialog(BookingDetailActivity.this, qrBitmap));

        binding.buttonBanner.setOnClickListener(view -> utils.showImageDialog(BookingDetailActivity.this, roomImageBitmap));
    }

    private void setupNetworkChecker() {
        networkChecker = new NetworkChecker(this);
        networkChecker.registerNetworkCallback();
        networkChecker.setNetworkCallbackListener(new NetworkChecker.NetworkCallbackListener() {
            @Override
            public void onNetworkAvailable() {
                runOnUiThread(() -> {
                    if (binding.enterRoomDate.getText().equals("-")) {
                        updateUIState(true, 0);
                    } else {
                        updateUIState(true, 1);
                    }
                });
            }

            @Override
            public void onNetworkUnavailable() {
                runOnUiThread(() -> {
                    if (binding.enterRoomDate.getText().equals("-")) {
                        updateUIState(false, 0);
                    } else {
                        updateUIState(false, 1);
                    }
                });
            }
        });
    }

    private void loadDataInBackGround() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (networkChecker.isInternetConnected()) {
                    String userId = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE).getString("Id", "");
                    OutsystemsAPI.RefreshBookingDetail(userId, getIntent().getStringExtra("bookingHash"), BookingDetailActivity.this, new OutsystemsAPI.DataLoadCallback() {
                        @Override
                        public void onDataLoaded() {
                            try (DbManager dbManager = new DbManager(BookingDetailActivity.this)) {
                                booking = DBBookingLocal.getBookingByHash(getIntent().getStringExtra("bookingHash"), dbManager.getWritableDatabase());
                                room = DBRoomLocal.getRoomById(booking.getRoomId(), dbManager.getReadableDatabase());
                                tickets = DBTicketLocal.getAllTicketsByBookingId(booking.getHash(), dbManager.getReadableDatabase());

                                for (Ticket ticket : tickets) {
                                    listData = ticketLisDataArray.stream().filter(t -> t.getUuid().equals(ticket.getId())).findFirst().orElse(null);
                                    if(listData == null) {
                                        listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                        ticketLisDataArray.add(listData);
                                    }
                                }

                                uiHandler.post(() -> {
                                    populateUIFromDatabase();
                                    binding.progressBar.setVisibility(View.GONE);
//                                    binding.linearLayoutTop.setVisibility(View.VISIBLE);
                                    binding.linearLayoutContent.setVisibility(View.VISIBLE);
                                });
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                uiHandler.post(() -> Toast.makeText(BookingDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(getApplicationContext(), "Failed to load data: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try (DbManager dbManager = new DbManager(BookingDetailActivity.this)) {
                        booking = DBBookingLocal.getBookingByHash(getIntent().getStringExtra("bookingHash"), dbManager.getWritableDatabase());
                        room = DBRoomLocal.getRoomById(booking.getRoomId(), dbManager.getReadableDatabase());
                        tickets = DBTicketLocal.getAllTicketsByBookingId(booking.getHash(), dbManager.getReadableDatabase());

                        for (Ticket ticket : tickets) {
                            listData = ticketLisDataArray.stream().filter(t -> t.getUuid().equals(ticket.getId())).findFirst().orElse(null);
                            if(listData == null) {
                                listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                ticketLisDataArray.add(listData);
                            }
                        }

                        uiHandler.post(() -> {
                            populateUIFromDatabase();
                            binding.progressBar.setVisibility(View.GONE);
//                            binding.linearLayoutTop.setVisibility(View.VISIBLE);
                            binding.linearLayoutContent.setVisibility(View.VISIBLE);
                        });
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        uiHandler.post(() -> Toast.makeText(BookingDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void updateUIState(boolean isNetworkAvailable, int state) {
        // 0 - Camera to read QR code
        // 1 - QR code to exit room
        switch (state) {
            case 0:
                if (isNetworkAvailable) {
                    binding.noWifiImage.setVisibility(View.GONE);
//                    binding.QRimage.setVisibility(View.GONE);
//                    binding.imageViewCaptured.setVisibility(View.VISIBLE);
//                    binding.qrCodeLabel.setText(R.string.read_qr);
                } else {
                    binding.noWifiImage.setVisibility(View.VISIBLE);
//                    binding.imageViewCaptured.setVisibility(View.GONE);
//                    binding.QRimage.setVisibility(View.GONE);
//                    binding.qrCodeLabel.setText(R.string.qr_failed);
                }
                break;
            case 1:
//                binding.imageViewCaptured.setVisibility(View.GONE);
                binding.noWifiImage.setVisibility(View.GONE);
//                binding.QRimage.setVisibility(View.VISIBLE);
//                binding.qrCodeLabel.setText(R.string.read_qrcode_exit);
                break;
        }
    }

    private void populateUIFromDatabase() {
        binding.toolbarTitle.setText(String.format("Reserva: %s", room != null ? room.getName() : ""));
        binding.startDate.setText(Utils.isDateNull(booking.getExpectedStartDate()) ? "-" : dateFormatHour.format(booking.getExpectedStartDate()) + "\n" + dateFormatDay.format(booking.getExpectedStartDate()));
        binding.endDate.setText(Utils.isDateNull(booking.getExpectedEndDate()) ? "-" : dateFormatHour.format(booking.getExpectedEndDate()) + "\n" + dateFormatDay.format(booking.getExpectedEndDate()));
        binding.enterRoomDate.setText(Utils.isDateNull(booking.getActualStartDate()) ? "-" : dateFormatHour.format(booking.getActualStartDate()) + "\n" + dateFormatDay.format(booking.getActualStartDate()));
        binding.exitRoomDate.setText(Utils.isDateNull(booking.getActualEndDate()) ? "-" : dateFormatHour.format(booking.getActualEndDate()) + "\n" + dateFormatDay.format(booking.getActualEndDate()));

        if (room != null) {
//            binding.roomImage.setImageBitmap(DBRoomImageLocal.getRoomImageByRoomId(room.getId(), new DbManager(this).getReadableDatabase()));
            roomImageBitmap = DBRoomImageLocal.getRoomImageByRoomId(room.getId(), new DbManager(this).getReadableDatabase());
            Drawable roomImageDrawable = new BitmapDrawable(getResources(), roomImageBitmap);
            binding.banner.setBackground(roomImageDrawable);
            binding.roomName.setText(room.getName());
        }

        if (!binding.enterRoomDate.getText().equals("-")) {
            Intent intent = getIntent();
            String hash = intent.getStringExtra("bookingHash");
            Utils u = new Utils();
            qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
            binding.QRimage.setImageBitmap(qrBitmap);
            //populate list of tickets
            populateTicketsLinearLayout(ticketLisDataArray);

            updateUIState(networkChecker.isInternetConnected(), 1);
        } else {
            updateUIState(networkChecker.isInternetConnected(), 0);
        }
    }

    private void populateTicketsLinearLayout(ArrayList<ListData> ticketListDataArray) {
        LinearLayout ticketsContainer = binding.ticketsContainer;
        ticketsContainer.removeAllViews(); // Limpa todos os itens se já existirem

        for (ListData ticketData : ticketListDataArray) {
            // Infla o layout do item do ticket
            View ticketItemView = LayoutInflater.from(this).inflate(R.layout.list_item, ticketsContainer, false);

            TextView titleTextView = ticketItemView.findViewById(R.id.idTitle);
            TextView descriptionTextView = ticketItemView.findViewById(R.id.idDescription);
            TextView statusTextView = ticketItemView.findViewById(R.id.idStatus);

            titleTextView.setText(ticketData.getTitle());
            descriptionTextView.setText(ticketData.getDescription());
            statusTextView.setBackgroundTintList(
                    ticketData.isSynchronized() ?
                            ColorStateList.valueOf(getResources().getColor(R.color.isSynchronized)) :
                            ColorStateList.valueOf(getResources().getColor(R.color.pending))
            );

            ticketItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BookingDetailActivity.this, CreateTicketActivity.class);
                    intent.putExtra("uuid", ticketData.getUuid());
                    startActivity(intent);
                }
            });

            // Adiciona a visualização do item ao container
            ticketsContainer.addView(ticketItemView);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        loadDataInBackGround();
        if (networkChecker != null) {
            networkChecker.registerNetworkCallback();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Desregistra o NetworkCallback
//        networkChecker.unregisterNetworkCallback();
        if (networkChecker != null) {
            networkChecker.unregisterNetworkCallback();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


    private void initBinding() {
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initViews() {
//        binding.imageViewCaptured.setOnClickListener(view -> checkPermissionAndShowActivity(this));
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
