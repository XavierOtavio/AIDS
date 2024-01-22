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

    private boolean isInternetConnected() {
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                boolean isConnectedToInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                boolean isCellularDataEnabled = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                boolean isEthernetConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

                // Retorna true se houver conexão à internet e pelo menos um dos tipos de transporte (móvel ou Ethernet) estiver disponível
                return isConnectedToInternet && (isCellularDataEnabled || isEthernetConnected);
            }
        }
        return false;
    }


}
