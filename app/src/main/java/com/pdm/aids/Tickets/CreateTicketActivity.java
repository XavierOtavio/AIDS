package com.pdm.aids.Tickets;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.pdm.aids.R;

import java.io.ByteArrayOutputStream;

public class CreateTicketActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button takePictureButton;
    private byte[] pictureByteArray;
    private DBTicketLocal dbTicketLocal;
    private ImageView capturedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        takePictureButton = findViewById(R.id.button_take_picture);
        capturedImageView = findViewById(R.id.image_view_captured);

        dbTicketLocal = new DBTicketLocal(this);

        takePictureButton.setOnClickListener(v -> dispatchTakePictureIntent());

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
                // No camera available, handle accordingly (show a message, disable functionality, etc.)
                Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pictureByteArray = getBytesFromBitmap(imageBitmap);

            capturedImageView.setVisibility(View.VISIBLE);
            capturedImageView.setImageBitmap(imageBitmap);
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

        if (!title.isEmpty() && !description.isEmpty()) {
            if (pictureByteArray == null) {
                pictureByteArray = new byte[0];
            }
            Ticket newTicket = new Ticket(title, description, pictureByteArray);
            boolean isInserted = dbTicketLocal.createTicket(newTicket);
            if (isInserted) {
                showToast("Ticket saved successfully");
                finish();
            } else {
                showToast("Failed to save ticket");
            }
        } else {
            showToast("Please fill in all necessary details");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
