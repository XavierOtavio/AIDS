package com.pdm.aids.Booking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.pdm.aids.R;

public class BookingShowQrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_show_qr);

        ImageView qr_image = findViewById(R.id.idQRImage);


    }

}