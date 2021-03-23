package com.example.practica4;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

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

public class MyAsyncTask extends AsyncTask<String, Void, Integer> {

    private MainActivity mActivity = null;

    public MyAsyncTask(MainActivity activity) {
        attach(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Integer doInBackground(String... params) {
        HttpURLConnection connection;

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
//                Log.d("MyAsyncTask", result);
                Gson gson = new Gson();
//                Map map = gson.fromJson(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8), Map.class);
                Map<String, Object> map = gson.fromJson(result, Map.class);
//                Log.d("MyAsyncTask", "" + map.entrySet().size());
                // Get photo object from photos entry
                List<Map<String, Object>> photoList =
                        ((List<Map<String, Object>>) ((Map) map.get("photos")).get("photo"));
                for (Map<String, Object> stringObjectMap : photoList) {
                    Log.d("MyAsyncTask", ((String) stringObjectMap.get("id")));
                }
                // Exclusion mutua
                synchronized (mActivity.getPublications()) {
                    mActivity.setPublications(photoList);
                }
            }
            return responseCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (mActivity == null)
            Log.i("MyAsyncTask", "Me salto onPostExecute() -- no hay nueva activity");
        else
            mActivity.setupAdapter(integer);
    }

    void detach() {
        this.mActivity = null;
    }

    void attach(MainActivity activity) {
        this.mActivity = activity;
    }
}
