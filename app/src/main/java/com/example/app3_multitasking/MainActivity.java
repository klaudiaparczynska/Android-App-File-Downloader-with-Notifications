package com.example.app3_multitasking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.CookieManager;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView adres, file_size, file_type, file_downloaded;
    Button download_info, download_file;
    private String INFO_ARRAY_LIST_KEY = "com.example.file_downloader2.INFO_ARRAY_LIST_KEY";
    //NotificationManager notificationMenager = getSystemService(NotificationManager.class);
    static final String ID_CHANNEL = "Notification";
    static final int ID_NOTIFICATION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adres = findViewById(R.id.adres);
        file_size = findViewById(R.id.file_size);
        file_type = findViewById(R.id.file_type);
        file_downloaded = findViewById(R.id.file_downloaded);
        download_file = findViewById(R.id.download_file);
        download_info = findViewById(R.id.download_info);
        //Set Default Address
        adres.setText("https://cdn.kernel.org/pub/linux/kernel/v4.x/patch-4.9.310.xz");
        //Async class Object
        Async async = new Async();
        //Notification
        //ExecutorService fileDownloadExecutor = Executors.newSingleThreadExecutor();
        //createNotificationChannel();


        download_info.setOnClickListener(v -> async.execute(adres.getText().toString()));
        download_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
    }
/*
    void createNotificationChannel() {
        notificationMenager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel channel1 = new NotificationChannel(ID_CHANNEL, name,NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("Progress Notification Channel");
            notificationMenager.createNotificationChannel(channel1);
        }
    }
    void downloadingFile(String url)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        request.setTitle(title);
        request.setDescription("Downloading...");
        String cookie = android.webkit.CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, title);
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(MainActivity.this, "Download started", Toast.LENGTH_SHORT).show();

    }
    void pom()
    {
        createNotificationChannel();
        String title = "Download";
        String content = "Download in Progress";
        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        //PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),ID_CHANNEL);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final int progressMaxValue = 100;
        int progressCurrentValue = 0;


        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(ID_NOTIFICATION, builder.build());
    }
    void createNotification()
    {

    }


 */




    private class Async extends AsyncTask<String, String, String> {
        int lenghtOfFile;
        String typeOfFile;

        @Override
        protected String doInBackground(String... f_url) {
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(f_url[0]);
                Log.i("URL:", f_url[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                lenghtOfFile = connection.getContentLength();
                typeOfFile = connection.getContentType();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }

            return typeOfFile;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            file_type.setText(result);
            file_size.setText(String.valueOf(lenghtOfFile));
        }
    }

}