package com.pdm.aids.Booking.BookingList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdm.aids.R;

import java.util.ArrayList;

public class BookingListAdapter extends ArrayAdapter<ListData> {
    BookingListActivity binding;

    public BookingListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList, BookingListActivity binding) {
        super(context, R.layout.list_item_booking, dataArrayList);
        this.binding = binding;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_booking, parent, false);
        }

        TextView roomName = view.findViewById(R.id.roomTitle_item);
        TextView expected_startDate = view.findViewById(R.id.expected_startDate);
        TextView expected_endDate = view.findViewById(R.id.expected_endDate);
        TextView bookingStatus = view.findViewById(R.id.bookingStatus);

        if (listData != null) {
            roomName.setText(String.valueOf(listData.roomName));
            expected_startDate.setText(String.valueOf(listData.expectedStartDate));
            expected_endDate.setText(String.valueOf(listData.expectedEndDate));
            if (listData.bookingStatusId == 3) {
                bookingStatus.setBackgroundTintList(getContext().getColorStateList(R.color.green));
                bookingStatus.setTextColor(getContext().getResources().getColor(R.color.green));
                bookingStatus.setText("Ativa");
            } else {
                bookingStatus.setBackgroundTintList(getContext().getColorStateList(R.color.orange));
                bookingStatus.setTextColor(getContext().getResources().getColor(R.color.orange));
                bookingStatus.setText("Reservada");
            }
        }
        return view;
    }
}
