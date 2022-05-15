package com.lavertis.project3.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.lavertis.project3.R;
import com.lavertis.project3.models.FileInfo;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsyncTask extends AsyncTask<String, Void, FileInfo> {
    private final WeakReference<Activity> weakActivity;

    public MyAsyncTask(Activity activity) {
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override
    protected FileInfo doInBackground(String... strings) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            Integer contentLength = connection.getContentLength();
            String contentType = connection.getContentType();
            return new FileInfo(contentLength, contentType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(FileInfo fileInfo) {
        super.onPostExecute(fileInfo);
        TextView fileSizeTextView = weakActivity.get().findViewById(R.id.fileSizeTextView);
        TextView fileTypeTextView = weakActivity.get().findViewById(R.id.fileTypeTextView);
        String fileSize = fileInfo.getSize() + " bytes";
        String fileType = fileInfo.getType();
        fileSizeTextView.setText(fileSize);
        fileTypeTextView.setText(fileType);
    }
}
