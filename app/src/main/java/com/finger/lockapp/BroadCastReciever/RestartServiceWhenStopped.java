package com.finger.lockapp.BroadCastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.finger.lockapp.Activities.MainActivity;
import com.finger.lockapp.Services.BackgroundManager;
import com.finger.lockapp.Utils.Utils;
import com.finger.lockapp.global.AppConstants;
import com.finger.lockapp.global.unitTesting;

public class RestartServiceWhenStopped extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
                BackgroundManager.getInstance().init(context).startService();
            }
        }
    }
}
