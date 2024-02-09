package com.pdm.aids.Booking.BookingHistory;

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

public class BookingHistoryAdapter extends ArrayAdapter<ListData> {
    BookingHistoryActivity binding;

    public BookingHistoryAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList, BookingHistoryActivity binding) {
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
//        ImageView roomImage = view.findViewById(R.id.roomImage_item);
        TextView expected_startDate = view.findViewById(R.id.expected_startDate);
        TextView expected_endDate = view.findViewById(R.id.expected_endDate);

        if (listData != null) {
            roomName.setText(String.valueOf(listData.roomName));
            expected_startDate.setText(String.valueOf(listData.expectedStartDate));
            expected_endDate.setText(String.valueOf(listData.expectedEndDate));
//            Bitmap bmp = listData.roomImage;
//            if (bmp != null) {
//                roomImage.setImageBitmap(bmp);
//            } else {
//                Toast.makeText(getContext(), "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
//            }
        }
        return view;
    }
}
