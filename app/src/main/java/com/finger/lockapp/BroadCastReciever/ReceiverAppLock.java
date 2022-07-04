package com.finger.lockapp.BroadCastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.finger.lockapp.Activities.MainActivity;
import com.finger.lockapp.Utils.Utils;
import com.finger.lockapp.global.AppConstants;
import com.finger.lockapp.global.unitTesting;

public class ReceiverAppLock extends BroadcastReceiver {
    private static final String TAG = "ReceiverAppLock";
    private String appRunning;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Gomaa ReceiverAppLock:  into the reciever");
        Utils utils = new Utils(context);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
             appRunning = utils.getLauncherTopApp();
        } else {
             appRunning = utils.getTopAppName(context);
        }
        if (utils.isLock(appRunning)) {
            Log.d(TAG, "Gomaa ReceiverAppLock:  the app is locked");
            if (!appRunning.equals(utils.getLastApp())) {
                Log.d(TAG, "Gomaa ReceiverAppLock:  this app is not equal the last app");
                utils.ClearLastApp();
                utils.setLastApp(appRunning);
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("broadcast_reciever", "broadcast_reciever");
                context.startActivity(i);
            } else {
                //try to get the last app key if it pass before or not
                // and when it pass we should remove the key
                Log.d(TAG, "Gomaa ReceiverAppLock:  this app is the last app");
            }
        }
    }

}
