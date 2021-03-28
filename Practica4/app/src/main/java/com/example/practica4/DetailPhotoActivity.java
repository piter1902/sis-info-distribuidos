package com.example.practica4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailPhotoActivity extends AppCompatActivity {

    private static final String TAG = "DetailPhotoActivity";
    private static final String IMAGEPATH = "https://live.staticflickr.com/";
    // Layout fields
    private ImageView imageView;
    private TextView titleView;
    private TextView descriptionView;

    // Photo ID
    private String id;

    // Fetch Async Task
    private DetailAsyncTask myTask = null;

    // Variable compartida con asyncTask
    private Map<String, Object> photoInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        // Get the intent
        Intent intent = getIntent();
        id = intent.getStringExtra("pubId");

        // link to the layout
        imageView = findViewById(R.id.imageView3);
        titleView = findViewById(R.id.detailTitle);
        descriptionView = findViewById(R.id.description);

        // Para obtener el valor devuelto en onRetainCustomNonConfigurationInstance
        myTask = (DetailAsyncTask) getLastCustomNonConfigurationInstance();

        if (myTask == null) {
            // Evita crear una AsyncTask cada vez que, por ejemplo, hay una rotación
            Log.i(TAG, "onCreate: About to create MyAsyncTask");
            myTask = new DetailAsyncTask(this);
            // Hacemos la petición a la API
            myTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.getInfo" +
                    "&api_key=e45df953d132e8de1fac638da4ed55bc&photo_id=" + id + "&format=json");
        } else {
            myTask.attach(this);
        }
    }

    // Source: https://stackoverflow.com/questions/31330122/android-navigate-back-to-activity-dont-reload-parent/31331757
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Prevent reload main activity
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Getters && seters
    public Map<String, Object> getPhotoInfo() {
        return photoInfo;
    }

    public void setPhotoInfo(Map<String, Object> photoInfo) {
        this.photoInfo = photoInfo;
    }

    public void setupAdapter(Integer integer) throws IOException {

        String descriptionContent;
        String titleContent;
        String imageUrl;
        String imageId;
        String imageSecret;
        String imageServer;

        if (integer == HttpURLConnection.HTTP_OK) {
            synchronized (getPhotoInfo()) {
                // Obtain info from Gson
                descriptionContent = ((String) ((Map) photoInfo.get("description")).get("_content"));
                titleContent = ((String) ((Map) photoInfo.get("title")).get("_content"));
                //imageUrl = ((String) ((Map) ((List) ((Map) photoInfo.get("urls")).get("url")).get(0)).get("_content"));
                imageId = ((String) photoInfo.get("id"));
                imageSecret = ((String) photoInfo.get("secret"));
                imageServer = ((String) photoInfo.get("server"));
                // Source: https://www.flickr.com/services/api/misc.urls.html
                imageUrl = IMAGEPATH + imageServer + "/" + imageId + "_" + imageSecret + ".jpg";

                Log.d(TAG, "Description: " + descriptionContent);
                Log.d(TAG, "title: " + titleContent);
                Log.d(TAG, "url: " + imageUrl);
            }

            // Actualizamos info en layout
            titleView.setText(!titleContent.isEmpty() ? titleContent : "No title");
            descriptionView.setText(!descriptionContent.isEmpty() ? descriptionContent : "No description");
            Picasso.get().load(imageUrl).into(imageView);
        }
    }
}