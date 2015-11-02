package ru.ifmo.android_2015.citycam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ru.ifmo.android_2015.citycam.model.City;
import ru.ifmo.android_2015.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private City city;

    ImageView camImageView;
    ProgressBar progressView;
    Button prevButton, nextButton;
    TextView textView;
    DownloadImageTask downloadImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressView = (ProgressBar) findViewById(R.id.progress);
        prevButton = (Button) findViewById(R.id.PrevButton);
        nextButton = (Button) findViewById(R.id.NextButton);
        textView = (TextView) findViewById(R.id.textView);

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                downloadImageTask.prevCamera();
                downloadImageTask.attachCamera();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                downloadImageTask.nextCamera();
                downloadImageTask.attachCamera();
            }
        });

        getSupportActionBar().setTitle(city.name);

        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.

        if(savedInstanceState != null) {
            downloadImageTask = (DownloadImageTask) getLastCustomNonConfigurationInstance();
        }

        if(downloadImageTask == null) {
            progressView.setVisibility(View.VISIBLE);
            downloadImageTask = new DownloadImageTask(this);
            try {
                downloadImageTask.execute(Webcams.createNearbyUrl(city.latitude, city.longitude));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            downloadImageTask.attachActivity(this);
            downloadImageTask.attachCamera();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainCustomNonConfigurationInstance() {
        return downloadImageTask;
    }

    static final String TAG = "CityCam";
}
