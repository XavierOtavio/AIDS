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
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbarTicketList.setNavigationOnClickListener(v -> finish());

        TextView textTitle_toolbar = binding.toolbarTicketTitle;
        textTitle_toolbar.setText("Suporte");

        ImageView add = binding.toolbarNewTicket;
        add.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
            startActivity(intent);
        });

        try (DbManager dataBaseHelper = new DbManager(this)) {
            List<Integer> statusIds = Arrays.asList(3);
            booking = new DBBookingLocal().getBookingsByStatus(statusIds, dataBaseHelper.getWritableDatabase());

            if (!booking.isEmpty()) {
                System.out.println("Booking Hash: " + booking.get(0).getHash());

                tickets = new DBTicketLocal().getAllTicketsByBookingId(booking.get(0).getHash(), dataBaseHelper.getWritableDatabase());

                if (!tickets.isEmpty()) {
                    System.out.println("Ticket modified date: " + tickets.get(0).getLastModified());

                    for (Ticket ticket : tickets) {
                        listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getLastModified(), ticket.getId());
                        dataArrayList.add(listData);
                        System.out.println("Added ticket to dataArrayList: " + listData.getTitle());
                    }

                    listAdapter = new ListAdapter(TicketListActivity.this, dataArrayList);
                    binding.listViewTicket.setAdapter(listAdapter);

                    binding.listViewTicket.setOnItemClickListener((parent, view, position, id) -> {
                        ListData clickedItem = dataArrayList.get(position);

                        System.out.println("Clicked on item with title: " + clickedItem.getTitle());

                        Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
                        intent.putExtra("uuid", clickedItem.getUuid());

                        startActivity(intent);
                    });
                } else {
                    System.out.println("No tickets found for the given booking.");
                }
            } else {
                System.out.println("No bookings found with the specified status.");
            }
        } catch (Exception e) {
            handleException("Error reading tickets", e);
        }
    }

    @Override
    protected void onResume() {
        updateDataList();
        if (listData != null) {
            listAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    private void updateDataList() {
        try (DbManager dataBaseHelper = new DbManager(this)) {
            List<Integer> statusIds = Arrays.asList(3);
            booking = new DBBookingLocal().getBookingsByStatus(statusIds, dataBaseHelper.getWritableDatabase());
            tickets = new DBTicketLocal().getAllTicketsByBookingId(booking.get(0).getHash(), dataBaseHelper.getWritableDatabase());

            dataArrayList.clear();
            for (Ticket ticket : tickets) {
                listData = new ListData(ticket.getTitle(), ticket.getDescription(), ticket.getLastModified(), ticket.getId());
                dataArrayList.add(listData);
                System.out.println(dataArrayList);
            }
        } catch (Exception e) {
            handleException("Error updating data list", e);
        }
    }

    private void handleException(String message, Exception e) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        System.out.println(e.getMessage());
        toast.show();
    }
}

