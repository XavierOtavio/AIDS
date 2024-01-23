package com.pdm.aids.Ticket.TicketDetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.R;
import com.pdm.aids.Room.DBRoomLocal;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateTicketActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView facility, expectedStart, expectedLeave;
    private CarouselAdapter adapter;
    private Button takePictureButton;
    private byte[] pictureByteArray;
    private ImageView camera, takenPicture;
    private List<Booking> booking;
    private DbManager dbManager;
    private List<byte[]> pictures;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        dbManager = new DbManager(this);
        booking = new DBBookingLocal().getBookingsByStatus(3, dbManager.getWritableDatabase());
        pictures = new ArrayList<>();

        ViewPager viewPager = findViewById(R.id.viewPager);

        adapter = new CarouselAdapter(this, pictures);
        viewPager.setAdapter(adapter);

        RelativeLayout cameraLayout = findViewById(R.id.cameraLayout);
        cameraLayout.setOnClickListener(v -> dispatchTakePictureIntent());

        // Left Arrow Button
        ImageButton btnLeftArrow = findViewById(R.id.btnLeftArrow);
        btnLeftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        });

        // Right Arrow Button
        ImageButton btnRightArrow = findViewById(R.id.btnRightArrow);
        btnRightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }
        });


        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);

        facility = findViewById(R.id.facility);
        expectedStart = findViewById(R.id.expectedStart);
        expectedLeave = findViewById(R.id.expectedLeave);

        facility.setText(new DBRoomLocal().getRoomById(booking.get(0).getRoomId(),dbManager.getWritableDatabase()).getName());
        expectedStart.setText(booking.get(0).getExpectedStartDate().toString());
        expectedLeave.setText(booking.get(0).getExpectedEndDate().toString());

        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> saveTicket());

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateTicketActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            PackageManager packageManager = getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else{
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
                        pictures.add(pictureByteArray);
                        adapter.notifyDataSetChanged();
                        camera.setVisibility(View.VISIBLE);

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

            if (isBookingValid(booking)) {
                if (!title.isEmpty() && !description.isEmpty()) {
                    if (pictures == null) {
                        pictureByteArray = new byte[0];
                    }

                    UUID uuid = UUID.randomUUID();
                    String id = uuid.toString();
                    System.out.println(id);

                    Ticket newTicket = new Ticket(id, booking.get(0).getHash(), 1, title, description);
                    for (byte[] picture: pictures
                         ) {
                        TicketImage ticketImage = new TicketImage(newTicket.getId(), "filename", picture);
                        boolean imageIsInserted = new DBTicketLocal().createTicketImage(ticketImage, this, dbManager.getWritableDatabase());
                    }
                    boolean ticketIsInserted = new DBTicketLocal().createTicket(newTicket, this, dbManager.getWritableDatabase());

                    if (ticketIsInserted) {
                        showToast("Ticket saved successfully");
                        finish();
                    } else {
                        showToast("Failed to save ticket");
                    }
                } else {
                    showToast("Please fill in all necessary details");
                }
            } else {
                showToast("There is no Active Booking");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error while saving ticket");
        }
    }

    private boolean isBookingValid(List<Booking> bookingList) {
        return bookingList != null && !bookingList.isEmpty();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}