package com.example.mobilelab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class NetworkChangeReceiver extends BroadcastReceiver {
    View view;

    NetworkChangeReceiver(View view) {
        super();
        this.view = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!isOnline(context)) {
                Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_LONG).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            } else {
                try {
                    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("network", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("network error", "" + e.getMessage());
                }
            }
        }
        return false;
    }
}