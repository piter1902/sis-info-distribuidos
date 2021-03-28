package com.example.practica4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class DetailAsyncTask extends AsyncTask<String, Void, Integer> {

    ImageView image;

    private DetailPhotoActivity dActivity = null;

    public DetailAsyncTask(DetailPhotoActivity activity) {
        attach(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Integer doInBackground(String... params) {
        HttpURLConnection connection;
        URL imageUrl;
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            // Response from URL
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
//                Log.d("MyAsyncTask", connection.getContent().toString());
                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = new InputStreamReader
                        (connection.getInputStream(), StandardCharsets.UTF_8)) {
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                }
                String result = textBuilder.toString();
                result = result.replace("jsonFlickrApi(", "");
                result = result.substring(0, result.length() - 1);

                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(result, Map.class);

                // Get photo object from photos entry
                Map<String, Object> detailedPhotoInfo =
                        (Map) map.get("photo");
                // Exclusion mutua
                synchronized (dActivity.getPhotoInfo()) {
                    dActivity.setPhotoInfo(detailedPhotoInfo);
                }
                return connection.getResponseCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (dActivity == null)
            Log.i("MyAsyncTask", "Me salto onPostExecute() -- no hay nueva activity");
        else {
            try {
                dActivity.setupAdapter(integer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void detach() {
        this.dActivity = null;
    }

    void attach(DetailPhotoActivity activity) {
        this.dActivity = activity;
    }
}

