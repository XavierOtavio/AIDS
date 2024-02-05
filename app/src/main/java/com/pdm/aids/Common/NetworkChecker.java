package com.pdm.aids.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

public class NetworkChecker {

    private Context context;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    public interface NetworkCallbackListener {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }

    private NetworkCallbackListener callbackListener;

    public NetworkChecker(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void setNetworkCallbackListener(NetworkCallbackListener listener) {
        this.callbackListener = listener;
    }

    public void registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    if (callbackListener != null) {
                        callbackListener.onNetworkAvailable();
                    }
                }

                @Override
                public void onLost(Network network) {
                    if (callbackListener != null) {
                        callbackListener.onNetworkUnavailable();
                    }
                }
            };
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
    }

    public void unregisterNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public boolean isInternetConnected() {
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }



}
