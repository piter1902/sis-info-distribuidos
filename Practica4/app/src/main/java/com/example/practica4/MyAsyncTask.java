package com.example.practica4;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class MyAsyncTask extends AsyncTask<String, Void, Integer>
{
    private MainActivity mActivity = null;
    public MyAsyncTask(MainActivity activity)
    {
        attach(activity);
    }
    @Override
    protected Integer doInBackground(String... params)
    {
        HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            Log.i("MyAsyncTask", "hola");
            return connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @Override
    protected void onPostExecute(Integer integer)
    {
        if (mActivity == null)
            Log.i("MyAsyncTask", "Me salto onPostExecute() -- no hay nueva activity");
        else
            mActivity.setupAdapter(integer);
    }
    void detach()
    {
        this.mActivity = null;
    }
    void attach(MainActivity activity)
    {
        this.mActivity = activity;
    }
}
