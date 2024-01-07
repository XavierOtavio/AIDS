package com.pdm.aids.Booking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        TextView roomId = view.findViewById(R.id.roomTitle);
        ImageView qrImage = view.findViewById(R.id.roomImage);
        TextView id = view.findViewById(R.id.idId);

        if (listData != null) {
            roomId.setText(String.valueOf(listData.roomId));
            byte[] qrCodeByteArray = listData.qrImage;

            Bitmap bmp = BitmapFactory.decodeByteArray(qrCodeByteArray, 0, qrCodeByteArray.length);
            if (bmp != null) {
                qrImage.setImageBitmap(bmp);
            } else {
                // Handle case where conversion failed or byte[] is empty
            }

            id.setText(listData.id);
        }
        return view;
    }
}
