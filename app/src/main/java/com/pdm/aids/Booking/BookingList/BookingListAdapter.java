package com.pdm.aids.Booking.BookingList;

import android.content.Context;
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

        TextView roomName = view.findViewById(R.id.roomTitle);
        ImageView roomImage = view.findViewById(R.id.roomImage);
        TextView expectedDate = view.findViewById(R.id.expected_datetime);

        if (listData != null) {
            roomName.setText(String.valueOf(listData.roomName));
            expectedDate.setText(String.valueOf(listData.expectedDate));
            //byte[] imageByteArray = listData.roomImage;

            //Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            //if (bmp != null) {
            //    roomImage.setImageBitmap(bmp);
            //} else {
                // Handle case where conversion failed or byte[] is empty
            //}
        }
        return view;
    }
}
