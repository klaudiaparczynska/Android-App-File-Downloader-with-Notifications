package com.lavertis.project3;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lavertis.project3.services.DownloadService;
import com.lavertis.project3.tasks.MyAsyncTask;

public class MainActivity extends AppCompatActivity {
    private EditText urlEditText;
    private TextView fileSizeTextView;
    private TextView fileTypeTextView;
    private TextView bytesDownloadedTextView;
    private ProgressBar progressBar;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int downloaded = bundle.getInt("downloaded");
            int total = bundle.getInt("total");
            String msg = downloaded + " / " + total + " bytes";
            bytesDownloadedTextView.setText(msg);
            progressBar.setMax(total);
            progressBar.setProgress(downloaded);
        }
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", urlEditText.getText().toString());
        outState.putString("fileSize", fileSizeTextView.getText().toString());
        outState.putString("fileType", fileTypeTextView.getText().toString());
        outState.putInt("bytesDownloaded", progressBar.getProgress());
        outState.putInt("bytesTotal", progressBar.getMax());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        urlEditText.setText(savedInstanceState.getString("url"));
        fileSizeTextView.setText(savedInstanceState.getString("fileSize"));
        fileTypeTextView.setText(savedInstanceState.getString("fileType"));

        int bytesDownloaded = savedInstanceState.getInt("bytesDownloaded");
        int bytesTotal = savedInstanceState.getInt("bytesTotal");
        String msg = bytesDownloaded + " / " + bytesTotal + " bytes";
        bytesDownloadedTextView.setText(msg);
        progressBar.setProgress(bytesDownloaded);
        progressBar.setMax(bytesTotal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.urlEditText);
        fileSizeTextView = findViewById(R.id.fileSizeTextView);
        fileTypeTextView = findViewById(R.id.fileTypeTextView);
        bytesDownloadedTextView = findViewById(R.id.bytesDownloadedTextView);
        progressBar = findViewById(R.id.progressBar);

        Button downloadInfoButton = findViewById(R.id.downloadInfoButton);
        downloadInfoButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            if (url.isEmpty()) {
                urlEditText.setError("URL is empty");
            } else {
                MyAsyncTask task = new MyAsyncTask(this);
                task.execute(url);
            }
        });

        Button downloadFileButton = findViewById(R.id.downloadFileButton);
        downloadFileButton.setOnClickListener(v -> {
            String url = urlEditText.getText().toString();
            if (url.isEmpty()) {
                urlEditText.setError("URL is empty");
                return;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                DownloadService.startActionDownloadFile(this, url);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });
    }
}