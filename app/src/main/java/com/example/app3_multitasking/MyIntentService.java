package com.example.app3_multitasking;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyIntentService extends Service {
    private static final String TAG =
            MyIntentService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID =
            "com.example.service.notification_channel";
    private static final String NOTIFICATION_CHANNEL_NAME =
            "com.example.service.notification_channel";
    private static final int NOTIFICATION_ID = 1;
    public static final String ACTION_BROADCAST =
            "com.example.service.broadcast";
    public static final String TIME_EXTRA =
            "com.example.service.broadcast.time";
    private NotificationManager mNotificationManager;
    private HandlerThread mServiceHandlerThread;
    private Handler mServiceThreadHandler;
    private Handler mMainThreadHandler;
    public MyIntentService() {
    }
    //wykonuje jednorazową konfigurację - niezależnie od tego czy
    //usługę uruchomiono za pomocą startService() czy bindService()
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mServiceHandlerThread = new HandlerThread(TAG);
        mServiceHandlerThread.start();
        mServiceThreadHandler =
                new Handler(mServiceHandlerThread.getLooper());
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Android O wymaga kanału powiadomień
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // utwórz kanał dla powiadomienia
            NotificationChannel mChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_LOW);
            // utwórz kanał powiadomień w menedżerze powiadomień
            mNotificationManager.createNotificationChannel(mChannel);
        } }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
    //trzeba zaimplementować (w najprostszym przypadku zwraca null -
    //unbound service)
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    //wywoływana po wywołaniu startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand()");
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }
    public void startTimer() {
        Log.d(TAG, "startTimer() - starting foreground");
        startForeground(NOTIFICATION_ID, getNotification(0));
        mServiceThreadHandler.post(() -> {
            for (int time = 0; time <= 30; time++) {
                Log.d(TAG, "startTimer()/lambda - current time: " +
                        time);
                updateNotification(time);
                sendTimeBroadcast(time);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } }
            mMainThreadHandler.post(() -> stopSelf());
        }); }
    //przesyłanie danych
    private void sendTimeBroadcast(int time) {
        Intent broadcastIntent = new Intent(ACTION_BROADCAST);
        broadcastIntent.putExtra(TIME_EXTRA, time);
        sendBroadcast(broadcastIntent);
        Log.d(TAG,"sendTimeBroadcast() - sent time: " + time +
                " using broadcast");
    }
    private Notification getNotification(int time) {
        Intent intent = new Intent(this, MyIntentService.class);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,
                        NOTIFICATION_CHANNEL_ID)
                        .setContentText("Notification text, time: " + time)
                        .setContentTitle("Notification title")
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("Notification ticker")
                        .setWhen(System.currentTimeMillis());
        return builder.build();
    }
    private void updateNotification(int time) {
        mNotificationManager.notify(NOTIFICATION_ID,
                getNotification(time));
    }
}

