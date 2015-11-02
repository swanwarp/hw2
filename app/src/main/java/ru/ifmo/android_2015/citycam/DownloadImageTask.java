package ru.ifmo.android_2015.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Матвей on 02.11.2015.
 */
public class DownloadImageTask extends AsyncTask<URL, Integer, Integer> implements ProgressCallback {

    ArrayList<Webcam> webcams;
    int current = 0;
    Bitmap[] image = new Bitmap[0];
    CityCamActivity currentActivity = null;

    DownloadImageTask(CityCamActivity activity) {
        this.currentActivity = activity;
    }

    void nextCamera() {
        if(webcams.size() != 0 && current < webcams.size() - 1) {
            current++;
            attachCamera();
        }
    }

    void prevCamera() {
        if(webcams.size() != 0 && current > 0) {
            current--;
            attachCamera();
        }
    }

    void attachCamera() {
        if(image.length == 0) {
            currentActivity.textView.setVisibility(View.VISIBLE);
            currentActivity.progressView.setVisibility(View.INVISIBLE);
            return;
        }
        currentActivity.camImageView.setImageBitmap(image[current]);
    }

    void attachActivity(CityCamActivity activity) {
        this.currentActivity = activity;
    }

    @Override
    protected Integer doInBackground(URL... URLS) {
        try {
            downloadFile(URLS[0].toString());
            return 0;
        } catch (Exception e) {
            Log.e(currentActivity.TAG, "Error downloading file: " + e, e);
            return 1;
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        publishProgress(progress);
    }

    @Override
    protected void onPostExecute (Integer resultCode) {
        // Этот метод выполняется в UI потоке
        // Параметр resultCode -- это результат doInBackground
        attachCamera();
        currentActivity.progressView.setVisibility(View.INVISIBLE);
    }

    void downloadFile(String downloadUrl) throws IOException {
        if(image.length != 0) return;
        HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
        InputStream in = null;
        try {
            in = conn.getInputStream();
            webcams = JsonParse.JsonReadWebcams(in);
            if(webcams != null && webcams.size() != 0 ) {
                image = new Bitmap[webcams.size()];
                for(current = 0; current < webcams.size(); current++) {
                    URL url = new URL(webcams.get(current).url);
                    conn = (HttpURLConnection) url.openConnection();
                    in = new BufferedInputStream(conn.getInputStream());
                    image[current] = BitmapFactory.decodeStream(in);
                }
                current = 0;
            }
        } finally {
            if(in != null) in.close();
            conn.disconnect();
        }
    }


}