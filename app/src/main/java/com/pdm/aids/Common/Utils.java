package com.pdm.aids.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pdm.aids.Login.LoginActivity;

import java.io.ByteArrayOutputStream;
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

}
