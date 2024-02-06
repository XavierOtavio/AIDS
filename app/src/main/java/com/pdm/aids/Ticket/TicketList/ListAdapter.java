package com.pdm.aids.Ticket.TicketList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdm.aids.R;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListData> {

    TicketListActivity binding;
    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList, TicketListActivity binding) {
        super(context, R.layout.list_item, dataArrayList);
        this.binding = binding;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView title = view.findViewById(R.id.idTitle);
        TextView description = view.findViewById(R.id.idDescription);
        TextView date = view.findViewById(R.id.idDate);
        TextView status = view.findViewById(R.id.idStatus);

        assert listData != null;
        title.setText(listData.getTitle());
        description.setText(listData.getDescription());
        date.setText(listData.getFormattedStartDate());

        // TODO: Set the status based on the actual status of the ticket.
        // For example, you can create a method in ListData like getStatus() and use it here.

        status.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pending)));
        status.setText("Pendente");

        return view;
    }
}

