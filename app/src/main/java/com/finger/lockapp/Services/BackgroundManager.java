package com.finger.lockapp.Services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.finger.lockapp.BroadCastReciever.RestartServiceWhenStopped;
import com.finger.lockapp.global.AppConstants;
import com.finger.lockapp.global.unitTesting;

public class BackgroundManager {
    public static final int period =15*10000;//15Minute
    public static final int ALARM_ID = 159874;
    private static BackgroundManager Instance;
    private Context context;

    public static BackgroundManager getInstance() {
        if (Instance == null) {
            Instance = new BackgroundManager();
        }
        return Instance;
    }

    public BackgroundManager init(Context c) {
        context = c;
        return this;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startService() {
        if (!isServiceRunning(LockService.class)) {
            Intent serivceintent = new Intent(context, LockService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serivceintent);
            } else {
                context.startService(serivceintent);
            }
        }
    }

    public void StopService(Class<?> serviceClass) {
        if (isServiceRunning(serviceClass)) {
            context.stopService(new Intent(context, serviceClass));
        }
    }


}
