package com.pdm.aids.Ticket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdm.aids.R;
import com.pdm.aids.databinding.ActivityTicketListBinding;

import java.util.ArrayList;


public class TicketListActivity extends AppCompatActivity {
    ActivityTicketListBinding binding;
    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();

    ArrayList<Ticket> tickets = new ArrayList<>();
    ListData listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        binding = ActivityTicketListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the root view of the binding object

        TextView textTitle_toolbar = findViewById(R.id.toolbar_title);
        textTitle_toolbar.setText("Suporte");

        ImageView add = findViewById(R.id.toolbar_add);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateTicketActivity.class);
            startActivity(intent);
        });

        try (DBTicketLocal dataBaseHelper = new DBTicketLocal(this)) {
            tickets = dataBaseHelper.getAllTickets();

            for (int i = 0; i < tickets.size(); i++) {
                listData = new ListData(tickets.get(i).getId(), tickets.get(i).getTitle(), tickets.get(i).getDescription());
                dataArrayList.add(listData);
            }


            listAdapter = new ListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    private void updateList() {
        try (DBTicketLocal dataBaseHelper = new DBTicketLocal(this)) {
            tickets = dataBaseHelper.getAllTickets();
            dataArrayList.clear();
            for (int i = 0; i < tickets.size(); i++) {
                listData = new ListData(tickets.get(i).getId(), tickets.get(i).getTitle(), tickets.get(i).getDescription());
                dataArrayList.add(listData);
            }

            listAdapter = new ListAdapter(this, dataArrayList, this);
            binding.listView.setAdapter(listAdapter);
            binding.listView.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading tickets", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}