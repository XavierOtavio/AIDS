package com.pdm.aids.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.Booking.BookingList.BookingListActivity;
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
}