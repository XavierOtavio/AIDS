package com.pdm.aids.Booking.BookingDetails;

import static com.pdm.aids.Booking.BookingHistory.BookingHistoryActivity.selectedBooking;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.BookingHistory.BookingHistoryActivity;
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
import com.pdm.aids.Room.RoomImage;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketDetails.CreateTicketActivity;
import com.pdm.aids.Ticket.TicketList.ListData;
import com.pdm.aids.databinding.ActivityBookingDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingDetailActivity extends AppCompatActivity {
    private ActivityBookingDetailBinding binding;
    private Booking booking, onlineBooking;
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
        binding.linearLayoutContent.setVisibility(View.GONE);
        onlineBooking = BookingHistoryActivity.selectedBooking;
        setupNetworkChecker();

        if (onlineBooking != null) {
            booking = BookingHistoryActivity.selectedBooking;
            loadDataInBackGround();
        } else {
            loadLocalDataInBackGround();
        }


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
        final int[] pendingTasks = {2};
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (networkChecker.isInternetConnected()) {
                    String userId = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE).getString("Id", "");
                    room = BookingHistoryActivity.selectedRoom;
                    OutsystemsAPI.getTicketsByBookingUUIDOnline(booking.getHash(), BookingDetailActivity.this, ticketArrayList -> {
                        tickets = ticketArrayList;
                        for (Ticket ticket : tickets) {
                            listData = ticketLisDataArray.stream().filter(t -> t.getUuid().equals(ticket.getId())).findFirst().orElse(null);

                            if (listData == null) {
                                listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                ticketLisDataArray.add(listData);
                            }
                        }
                        pendingTasks[0]--;
                        UI_Run(pendingTasks);
                    });
                    OutsystemsAPI.getRoomImageOnline(room.getId(), BookingDetailActivity.this, new OutsystemsAPI.RoomImageCallback() {
                        @Override
                        public void onRoomImageReceived(RoomImage roomImage) {
                            roomImageBitmap = roomImage.getImageBitmap();
                            uiHandler.post(() -> {
                                        Drawable roomImageDrawable = new BitmapDrawable(getResources(), roomImageBitmap);
                                        binding.banner.setBackground(roomImageDrawable);
                                        binding.roomName.setText(room.getName());
                                    }
                            );
                            pendingTasks[0]--;
                            UI_Run(pendingTasks);
                        }

                        @Override
                        public void onError(String error) {
                            throw new RuntimeException("Error: " + error);
                        }
                    });
                } else {
                    onDestroy();
                }
            }
        });
    }

    private void loadLocalDataInBackGround() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (networkChecker.isInternetConnected()) {
                    String userId = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE).getString("Id", "");
                    OutsystemsAPI.RefreshBookingDetail(userId, getIntent().getStringExtra("bookingHash"), getIntent().getIntExtra("bookingId", 0), BookingDetailActivity.this, new OutsystemsAPI.DataLoadCallback() {
                        @Override
                        public void onDataLoaded() {
                            try (DbManager dbManager = new DbManager(BookingDetailActivity.this)) {
                                booking = DBBookingLocal.getBookingByHash(getIntent().getStringExtra("bookingHash"), dbManager.getWritableDatabase());
                                room = DBRoomLocal.getRoomById(booking.getRoomId(), dbManager.getReadableDatabase());
                                tickets = DBTicketLocal.getAllTicketsByBookingId(booking.getHash(), dbManager.getReadableDatabase());

                                for (Ticket ticket : tickets) {
                                    listData = ticketLisDataArray.stream().filter(t -> t.getUuid().equals(ticket.getId())).findFirst().orElse(null);
                                    if (listData == null) {
                                        listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                        ticketLisDataArray.add(listData);
                                    }
                                }

                                uiHandler.post(() -> {
                                    populateUIFromDatabase(false);
                                    binding.progressBar.setVisibility(View.GONE);
                                    binding.linearLayoutContent.setVisibility(View.VISIBLE);
                                });
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                uiHandler.post(() -> Toast.makeText(BookingDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                throw new RuntimeException("Error: " + e.getMessage());
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
                            ticketLisDataArray = ticketLisDataArray.stream().map(t -> {
                                if (t.getUuid().equals(ticket.getId()) && t.isSynchronized() != ticket.getIsSynchronized()) {
                                    return new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                }
                                return t;
                            }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                            if (ticketLisDataArray.stream().noneMatch(t -> t.getUuid().equals(ticket.getId()))) {
                                listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getCreationDate(), ticket.getId(), ticket.getIsSynchronized());
                                ticketLisDataArray.add(listData);
                            }
                        }

                        uiHandler.post(() -> {
                            populateUIFromDatabase(false);
                            binding.progressBar.setVisibility(View.GONE);
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
                } else {
                    binding.noWifiImage.setVisibility(View.VISIBLE);
                }
            case 1:
                binding.noWifiImage.setVisibility(View.GONE);
                break;
        }
    }

    public void UI_Run(int[] count) {
        if (count[0] == 0) {
            executorService.submit(new Runnable() {
                public void run() {
                    uiHandler.post(() ->

                    {
                        populateUIFromDatabase(true);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.linearLayoutContent.setVisibility(View.VISIBLE);
                    });
                }
            });
        }
    }

    private void populateUIFromDatabase(boolean fromHistory) {

        System.out.println(1);
        binding.toolbarTitle.setText(String.format("Reserva: %s", room != null ? room.getName() : ""));
        binding.startDate.setText(Utils.isDateNull(booking.getExpectedStartDate()) ? "--:--\n--/--/----" : dateFormatHour.format(booking.getExpectedStartDate()) + "\n" + dateFormatDay.format(booking.getExpectedStartDate()));
        binding.endDate.setText(Utils.isDateNull(booking.getExpectedEndDate()) ? "--:--\n--/--/----" : dateFormatHour.format(booking.getExpectedEndDate()) + "\n" + dateFormatDay.format(booking.getExpectedEndDate()));
        binding.enterRoomDate.setText(Utils.isDateNull(booking.getActualStartDate()) ? "--:--\n--/--/----" : dateFormatHour.format(booking.getActualStartDate()) + "\n" + dateFormatDay.format(booking.getActualStartDate()));
        binding.exitRoomDate.setText(Utils.isDateNull(booking.getActualEndDate()) ? "--:--\n--/--/----" : dateFormatHour.format(booking.getActualEndDate()) + "\n" + dateFormatDay.format(booking.getActualEndDate()));

        //populate list of tickets
        if (ticketLisDataArray.size() > 0) {
            populateTicketsLinearLayout(ticketLisDataArray);
        } else {
            binding.ticketsContainer.setVisibility(View.GONE);
            binding.ticketListEmpty.setVisibility(View.VISIBLE);
        }

        if (room != null && !fromHistory) {
            roomImageBitmap = DBRoomImageLocal.getRoomImageByRoomId(room.getId(), new DbManager(this).getReadableDatabase());
            Drawable roomImageDrawable = new BitmapDrawable(getResources(), roomImageBitmap);
            binding.banner.setBackground(roomImageDrawable);
            binding.roomName.setText(room.getName());
        }

        switch (booking.getBookingStatusId()) {
            case 1:
                binding.bookingStatus.setText("Expirada");
                binding.bookingStatus.setTextColor(getResources().getColor(R.color.grey));
                binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.grey));
                break;
            case 2:
                binding.bookingStatus.setText("Terminada");
                binding.bookingStatus.setTextColor(getResources().getColor(R.color.lightBlue));
                binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.lightBlue));
                break;
            case 3:
                binding.bookingStatus.setText("Ativa");
                binding.bookingStatus.setTextColor(getResources().getColor(R.color.green));
                binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                Intent intent = getIntent();
                String hash = intent.getStringExtra("bookingHash");
                Utils u = new Utils();
                qrBitmap = BitmapFactory.decodeByteArray(u.getQrImage(hash), 0, u.getQrImage(hash).length);
                binding.QRimage.setImageBitmap(qrBitmap);
                binding.qrCodeContainer.setVisibility(View.VISIBLE);
                binding.reportButtonContainer.setVisibility(View.VISIBLE);
                break;
            case 4:
                binding.bookingStatus.setText("Reservada");
                binding.bookingStatus.setTextColor(getResources().getColor(R.color.orange));
                binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.orange));
                break;
            case 5:
                binding.bookingStatus.setText("Cancelada");
                binding.bookingStatus.setTextColor(getResources().getColor(R.color.red));
                binding.bookingStatus.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                break;
            default:
                break;
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
                    boolean online = getIntent().getBooleanExtra("online", false);
                    intent.putExtra("online", online);
                    startActivity(intent);
                }
            });

            // Adiciona a visualização do item ao container
            ticketsContainer.addView(ticketItemView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (networkChecker != null) {
            networkChecker.registerNetworkCallback();
            if (onlineBooking == null) {
                loadLocalDataInBackGround();
                populateTicketsLinearLayout(ticketLisDataArray);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Desregistra o NetworkCallback
//        networkChecker.unregisterNetworkCallback();
        selectedBooking = null;
        if (networkChecker != null) {
            networkChecker.unregisterNetworkCallback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedBooking = null;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


    private void initBinding() {
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initViews() {
        binding.toolbarMain.setNavigationOnClickListener(v -> finish());
    }
}
