package com.pdm.aids.Ticket.TicketList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.Booking.Booking;
import com.pdm.aids.Booking.DBBookingLocal;
import com.pdm.aids.Common.DbManager;
import com.pdm.aids.R;
import com.pdm.aids.Ticket.DBTicketLocal;
import com.pdm.aids.Ticket.Ticket;
import com.pdm.aids.Ticket.TicketDetails.CreateTicketActivity;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TicketListActivity extends AppCompatActivity {
    ActivityTicketListBinding binding;
    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();

    ArrayList<Ticket> tickets = new ArrayList<>();
    ListData listData;
    List<Booking> booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbarTicketList.setNavigationOnClickListener(v -> finish());

        TextView textTitle_toolbar = findViewById(R.id.toolbar_ticket_title);
        textTitle_toolbar.setText("Suporte");


        ImageView add = findViewById(R.id.toolbar_new_ticket);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
            startActivity(intent);
        });

        try (DbManager dataBaseHelper = new DbManager(this)){
            List<Integer> statusIds = Arrays.asList(3);

            booking = new DBBookingLocal().getBookingsByStatus(statusIds,dataBaseHelper.getWritableDatabase());
            tickets = new DBTicketLocal().getAllTicketsByBookingId(booking.get(0).getHash(), dataBaseHelper.getWritableDatabase());

            for (int i = 0; i < tickets.size(); i++) {

                listData = new ListData(tickets.get(i).getTitle(), tickets.get(i).getDescription());
                dataArrayList.add(listData);
            }

            listAdapter = new ListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            System.out.println(e.getMessage());
            toast.show();
        }
    }
    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }
    private void updateList() {
        try (DbManager dataBaseHelper = new DbManager(this)) {
            booking = new DBBookingLocal().getAllBookings(dataBaseHelper.getWritableDatabase());
            tickets = new DBTicketLocal().getAllTicketsByBookingId(booking.get(2).getHash(), dataBaseHelper.getWritableDatabase());

            dataArrayList.clear();
            for (int i = 0; i < tickets.size(); i++) {
                listData = new ListData(tickets.get(i).getTitle(), tickets.get(i).getDescription());
                dataArrayList.add(listData);
            }

            listAdapter = new ListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            System.out.println(e.getMessage());
            toast.show();
        }
    }
}