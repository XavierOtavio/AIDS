package com.pdm.aids.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pdm.aids.Login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static Date convertUnixToDate(String unix) {
        try {
            Date date = new Date(Long.parseLong(unix) * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateFormat.format(date));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Date convertLongToStringDate(long timestamp) {
        try {
            Date date = new Date(Long.parseLong(String.valueOf(timestamp)) * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateFormat.format(date));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing date string", e);
        }
    }


    public static boolean isDateNull(Date dateToCheck) {
        try {
            if (dateToCheck != null) {
                Date referenceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("1900-01-01 00:00:00");
                return dateToCheck.equals(referenceDate);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getQrImage(String hash){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(hash,
                    BarcodeFormat.QR_CODE,
                    300,
                    300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean checkInternetConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Verifica se o ConnectivityManager não é nulo (garante que o serviço está disponível)
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();

            // Verifica se a rede ativa não é nula
            if (network != null) {
                // Obtém as capacidades da rede ativa (por exemplo, WiFi, mobile, Ethernet)
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);

                // Verifica se as capacidades da rede incluem WiFi, celular ou Ethernet
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;

    }

    public static String addImageToLocalStorage(String path, JSONObject img, Context context) throws JSONException, IOException {
        String folderPath = context.getFilesDir() + "/" + path;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                new Exception("Failed to create folder");
            }
        }
        String filePath = folderPath + "/" + img.getString("Filename");
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            String base64Image = img.getString("Image");
            Bitmap bmp = imageConvert(base64Image);

            FileOutputStream stream = new FileOutputStream(filePath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            bmp.recycle();
        }
        return filePath;
    }
    public static Bitmap imageConvert(String base64Image) throws IllegalArgumentException
    {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bmp;
    }

    public static String imageConvert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
