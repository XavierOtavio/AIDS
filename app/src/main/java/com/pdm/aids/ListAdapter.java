package com.pdm.aids;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListData> {

    Home binding;

    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList, Home binding) {
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
        TextView id = view.findViewById(R.id.idId);
        TextView status = view.findViewById(R.id.idStatus);
        assert listData != null;
        title.setText(listData.title);
        description.setText(listData.description);
        id.setText(listData.id);

        //TODO: Alterar isto para o estado do ticket
        status.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.pending)));
        status.setText("Pendente");

        return view;
    }
}