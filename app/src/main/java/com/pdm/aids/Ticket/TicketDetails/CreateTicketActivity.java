package com.pdm.aids.Ticket.TicketDetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.Common.OutsystemsAPI;
import com.pdm.aids.Common.Utils;
import com.pdm.aids.Login.LoginActivity;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Room.Room;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateTicketActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText titleEditText, descriptionEditText;
    private TextView facility, createdDateTime, toolBarTitle, readOnlyTitle, readOnlyDescription;
    private CarouselAdapter adapter;
    private ViewPager viewPager;
    private RelativeLayout layoutSave, layoutCancel, cameraLayout;
    private ImageButton btnLeftArrow, btnRightArrow;
    private Button takePictureButton, buttonSave, buttonCancel;
    private byte[] pictureByteArray;
    private ImageView camera, takenPicture;
    private Booking booking;
    private DbManager dbManager;
    public String title, description;
    public Date createdDate;
    public Ticket ticket;
    public ArrayList<TicketImage> ticketImages = new ArrayList<>();
    private List<Bitmap> pictures = new ArrayList<>();
    private List<String> picturesToSend;
    private String userId;
    private String uuid;
    SimpleDateFormat dateFormatHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        Intent intent = getIntent();
        uuid = intent.getStringExtra("uuid");
        boolean online = intent.getBooleanExtra("online", false);
        dbManager = new DbManager(this);
        if (uuid != null) {
            if (online) {
                OutsystemsAPI.getTicketById(uuid, this, dbManager.getWritableDatabase(), new OutsystemsAPI.OnlineTicketCallback() {
                    @Override
                    public void onTicketImageReceived(Ticket ticketOnline, Room roomOnline, ArrayList<TicketImage> ticketImageArrayList) {
                        ticket = ticketOnline;
                        title = ticket.getTitle();
                        description = ticket.getDescription();
                        createdDate = ticket.getCreationDate();
                        createdDateTime.setText(dateFormatHour.format(createdDate) + "\n" + dateFormatDay.format(createdDate));

                        facility.setText(roomOnline.getName());

                        titleEditText.setVisibility(View.GONE);
                        descriptionEditText.setVisibility(View.GONE);
                        readOnlyTitle.setVisibility(View.VISIBLE);
                        readOnlyDescription.setVisibility(View.VISIBLE);
                        readOnlyTitle.setText(title);
                        readOnlyDescription.setText(description);

                        buttonSave.setVisibility(View.GONE);
                        layoutSave.setVisibility(View.GONE);
                        buttonCancel.setVisibility(View.GONE);
                        layoutCancel.setVisibility(View.GONE);
                        cameraLayout.setVisibility(View.GONE);


                        ticketImages = ticketImageArrayList;
                        if (ticketImages != null && !ticketImages.isEmpty()) {
                            for (TicketImage ticketImage : ticketImages) {
                                Bitmap bitmap = Utils.imageConvert(ticketImage.getImage());
                                pictures.add(bitmap);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            viewPager.setVisibility(View.GONE);
                            btnLeftArrow.setVisibility(View.GONE);
                            btnRightArrow.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        throw new RuntimeException("Error getting ticket: " + error);
                    }
                });
            } else {
                try {
                    ticket = new DBTicketLocal().getTicketByUUID(uuid, dbManager.getWritableDatabase());
                    title = ticket.getTitle();
                    description = ticket.getDescription();
                    createdDate = ticket.getCreationDate();
                    List<Integer> statusIds = Arrays.asList(3);
                    booking = new DBBookingLocal().getBookingByHash(ticket.getBookingId(), dbManager.getWritableDatabase());
                    pictures = new ArrayList<>();
                    picturesToSend = new ArrayList<>();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            String bookingHash = intent.getStringExtra("bookingHash");
            booking = new DBBookingLocal().getBookingByHash(bookingHash, dbManager.getWritableDatabase());
            title = "";
            description = "";
            createdDate = new Date();
            pictures = new ArrayList<>();
            picturesToSend = new ArrayList<>();
        }

        btnLeftArrow = findViewById(R.id.btnLeftArrow);
        btnLeftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        btnRightArrow = findViewById(R.id.btnRightArrow);
        btnRightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }
        });

        userId = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE)
                .getString("Id", "");

        viewPager = findViewById(R.id.viewPager);

        adapter = new CarouselAdapter(this, pictures);
        viewPager.setAdapter(adapter);
        if (!online) {
            List<String> paths = new DBTicketLocal().getImagePath(uuid, dbManager.getWritableDatabase());
            if (paths != null) {
                viewPager.setVisibility(View.VISIBLE);
                btnLeftArrow.setVisibility(View.VISIBLE);
                btnRightArrow.setVisibility(View.VISIBLE);
                for (String imagePath : paths) {

                    File imgFile = new File(imagePath);

                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        pictures.add(bitmap);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Image Loading", "Image file does not exist: " + imagePath);
                    }
                }
            } else {
                viewPager.setVisibility(View.GONE);
                btnLeftArrow.setVisibility(View.GONE);
                btnRightArrow.setVisibility(View.GONE);
            }
        }


        cameraLayout = findViewById(R.id.cameraLayout);
        cameraLayout.setOnClickListener(v -> dispatchTakePictureIntent());


        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        toolbar = findViewById(R.id.toolbar_main);
        toolBarTitle = findViewById(R.id.toolbar_title);
        readOnlyTitle = findViewById(R.id.readOnlyTitle);
        readOnlyDescription = findViewById(R.id.readOnlyDescription);
        layoutSave = findViewById(R.id.layout_save);
        layoutCancel = findViewById(R.id.layout_cancel);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);
        createdDateTime = findViewById(R.id.createdDateTime);


        if (uuid == null) {
            toolBarTitle.setText("Reportar Problema");
            createdDateTime.setText(dateFormatHour.format(new Date()) + "\n" + dateFormatDay.format(new Date()));
        } else {
            toolBarTitle.setText("Detalhes do Problema");
            createdDateTime.setText(Utils.isDateNull(createdDate) ? "--:--\n--/--/----" : dateFormatHour.format(createdDate) + "\n" + dateFormatDay.format(createdDate));
        }


        if (!online) {
            titleEditText.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            readOnlyTitle.setVisibility(View.GONE);
            readOnlyDescription.setVisibility(View.GONE);
            titleEditText.setText(title);
            descriptionEditText.setText(description);
        }

        facility = findViewById(R.id.facility);
        if (!online) {
            facility.setText(new DBRoomLocal().getRoomById(booking.getRoomId(), dbManager.getWritableDatabase()).getName());
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        buttonCancel.setOnClickListener(v -> finish());
        buttonSave.setOnClickListener(v -> saveTicket());
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateTicketActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            PackageManager packageManager = getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                pictureByteArray = getBytesFromBitmap(imageBitmap);
                picturesToSend.add(Base64.getEncoder().encodeToString(pictureByteArray));
                pictures.add(imageBitmap);
                System.out.println("fotos tiradas " + pictures);
                viewPager.setVisibility(View.VISIBLE);
                btnLeftArrow.setVisibility(View.VISIBLE);
                btnRightArrow.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                showToast("Failed to capture image");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void saveTicket() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        try {

            if (booking != null)
                if (!title.isEmpty() && !description.isEmpty()) {
                    if (picturesToSend == null) {
                        pictureByteArray = new byte[0];
                    }
                    Ticket ticket = new Ticket();
                    ArrayList<TicketImage> allImages = new ArrayList<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                    if (uuid == null) {
                        UUID uuid = UUID.randomUUID();
                        String id = uuid.toString();

                        ticket.setId(id);
                        ticket.setBookingId(booking.getHash());
                        ticket.setIsSynchronized(false);
                        ticket.setTitle(title);
                        ticket.setDescription(description);
                        ticket.setCreationDate(new Date(System.currentTimeMillis()));
                        ticket.setLastModified(new Date(System.currentTimeMillis()));
                    } else {
                        ticket = new DBTicketLocal().getTicketByUUID(uuid, dbManager.getWritableDatabase());
                        ticket.setTitle(title);
                        ticket.setIsSynchronized(false);
                        ticket.setDescription(description);
                        ticket.setLastModified(new Date(System.currentTimeMillis()));
                    }
                    for (int i = 0; i < picturesToSend.size(); i++) {
                        JSONObject img = new JSONObject();
                        img.put("Filename", ticket.getId() + "_" + i + ".jpg");
                        img.put("Image", picturesToSend.get(i));
                        String path = "ticketImages";
                        String filepath = Utils.addImageToLocalStorage(path, img, this);

                        TicketImage ticketImage = new TicketImage(
                                ticket.getId(),
                                ticket.getId() + "_" + i + ".jpg",
                                filepath,
                                dateFormat.parse(dateFormat.format(new Date(System.currentTimeMillis()))),
                                false);
                        boolean imageIsInserted = new DBTicketLocal().createOrUpdateTicketImage(ticketImage, this, dbManager.getWritableDatabase());

                        allImages.add(ticketImage);
                    }
                    ticket.setTicketImages(allImages);
                    boolean ticketIsInserted;
                    ticketIsInserted = new DBTicketLocal().createOrUpdateTicket(ticket, this, dbManager.getWritableDatabase());
                    if (ticketIsInserted) {
                        showToast("Ticket saved successfully");
                        finish();
                    } else {
                        showToast("Failed to save ticket");
                    }
                } else {
                    showToast("Please fill in all necessary details");
                }
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isBookingValid(List<Booking> bookingList) {
        return bookingList != null && !bookingList.isEmpty();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
