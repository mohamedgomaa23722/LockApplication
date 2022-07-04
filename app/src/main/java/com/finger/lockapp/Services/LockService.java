package com.finger.lockapp.Services;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.finger.lockapp.Activities.MainActivity;
import com.finger.lockapp.Utils.Utils;

import java.util.List;
import java.util.SortedMap;

import java.util.TreeMap;


public class LockService extends Service {
    Context context;
    private String locked_app = null;
    private String current_app = null;
    private int mInterval = 700;
    private Handler mHandler;
    private Utils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        StartMyforground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mHandler = new Handler();
        startRepeatingTask();
        return START_STICKY;
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    context = LockService.this;
                    utils = new Utils(context);
                    current_app = getRecentApps(context);
                    //  Log.d("current app:",current_app);
                    boolean lock_apps = utils.isLock(current_app);
                    if (lock_apps) {
                        if (!current_app.equals(utils.getLastApp()) && !current_app.equals("com.finger.lockapp")) {
                            utils.ClearLastApp();
                            utils.setLastApp(current_app);
                            locked_app = getRecentApps(context);
                            startUnlockActivity(getRecentApps(context));
                        }
                    }
                    if (!lock_apps) {
                        if (!current_app.contains("com.finger.lockapp") && !current_app.equals(locked_app)) {
                            utils.ClearLastApp();
                            utils.setLastApp(current_app);
                        }
                        if (current_app.equals(locked_app)) {

                        }
                    }

                } catch (Exception e) {

                }

            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public String getRecentApps(Context context) {
        String topPackageName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            topPackageName = componentInfo.getPackageName();
        }
        return topPackageName;
    }

    public void startUnlockActivity(String packagename) {
        Intent i = new Intent(LockService.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("broadcast_reciever", "broadcast_reciever");
        startActivity(i);
    }

    public void StartMyforground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            String NOTIFICATION_CHANNEL_ID = "example.permanence";
            String channelName = "Background Service";
            NotificationChannel chan = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                manager.createNotificationChannel(chan);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App Lock is running")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
}