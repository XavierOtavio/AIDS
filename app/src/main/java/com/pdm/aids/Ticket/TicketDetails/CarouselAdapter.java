package com.pdm.aids.Ticket.TicketDetails;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.pdm.aids.R;

import java.util.List;

public class CarouselAdapter extends PagerAdapter {
    Context context;
    List<byte[]> images;
    LayoutInflater layoutInflater;

    public CarouselAdapter(Context context, List<byte[]> images) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==((LinearLayout)object);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = layoutInflater.inflate(R.layout.carousel_item, container, false);

        ImageView imageView = v.findViewById(R.id.imageView);

        byte[] imageData = images.get(position);

        if (imageData != null && imageData.length > 0) {
            System.out.println(imageData);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            System.out.println(bitmap);
            imageView.setImageBitmap(bitmap);
        }

        container.addView(v);

        imageView.setOnClickListener(view -> showImageDialog(position));
        return v;
    }

    private void showImageDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);

        ImageView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
        byte[] imageData = images.get(position);

        if (imageData != null && imageData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            int maxWidth = 800;
            int maxHeight = 1200;
            bitmap = scaleBitmap(bitmap, maxWidth, maxHeight);
            enlargedImageView.setImageBitmap(bitmap);
        }

        dialog.show();
    }



    private Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = Math.min((float) maxWidth / width, (float) maxHeight / height);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }




    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
