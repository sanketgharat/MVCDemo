package com.example.sanket.mvcdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sanket.mvcdemo.utils.Functions;
import com.example.sanket.mvcdemo.view.MainActivity;

public class ConnectionListener extends BroadcastReceiver {
    public static final String TAG = "InternetConnReceiver";
    MainActivity main = null;

    public void setMainActivityHandler(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if(context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
        }*/
        if (main != null) {
            if (Functions.isNetworkAvailable(context)) {
                Log.d(TAG, "onReceive");
                main.onNetworkChange(true);
            } else {
                main.onNetworkChange(false);
            }
        }

    }
}
