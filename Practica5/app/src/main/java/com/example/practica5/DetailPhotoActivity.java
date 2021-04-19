package com.example.practica5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class DetailPhotoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DetailPhotoActivity";
    private static final String IMAGEPATH = "https://live.staticflickr.com/";
    // Layout fields
    private ImageView imageView;
    private TextView titleView;
    private TextView descriptionView;
    private MapView mapView;

    // Instancia de gmap
    private GoogleMap gmap;

    // Latitud & Longitud del usuario
    private String lat;
    private String lon;

    // Latitud & Longitud de la foto
    private String imgLat;
    private String imgLon;

    // Fetch Async Task
    private MyAsyncTask myTask = null;

    // Variable compartida con asyncTask
    private Map<String, Object> photoInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        // Get the intent
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");

        // link to the layout
        imageView = findViewById(R.id.imageView3);
        titleView = findViewById(R.id.detailTitle);
        descriptionView = findViewById(R.id.description);
        mapView = findViewById(R.id.mapView);


        // Una vez obtenida la location, llamamos a asynctask para que realice la peticion a flickr
        myTask = (MyAsyncTask) getLastCustomNonConfigurationInstance();

        if (myTask == null) {
            // Evita crear una AsyncTask cada vez que, por ejemplo, hay una rotación

            myTask = new MyAsyncTask(DetailPhotoActivity.this);

            // Creamos la uri para realizar la petición
            Uri queryFlickr = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("api_key", "e45df953d132e8de1fac638da4ed55bc")
                    .appendQueryParameter("method", "flickr.photos.search")
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lon", lon)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("extras", "url_s,description,geo")
                    .build();
            Log.i("MainActivity", "Query: " + queryFlickr.toString());
            // Hacemos la petición a la API
            myTask.execute(queryFlickr.toString());
        } else {
            myTask.attach(DetailPhotoActivity.this);
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
        //        String imageId;
//        String imageSecret;
//        String imageServer;

        if (integer == HttpURLConnection.HTTP_OK) {
            synchronized (getPhotoInfo()) {
                // Obtain info from Gson
                descriptionContent = ((String) ((Map) photoInfo.get("description")).get("_content"));
                titleContent = (String) (photoInfo.get("title"));
                imgLat = (String) (photoInfo.get("latitude"));
                imgLon = (String) (photoInfo.get("longitude"));
//                imageUrl = ((String) ((Map) ((List) ((Map) photoInfo.get("urls")).get("url")).get(0)).get("_content"));
//                imageId = ((String) photoInfo.get("id"));
//                imageSecret = ((String) photoInfo.get("secret"));
//                imageServer = ((String) photoInfo.get("server"));
                // Source: https://www.flickr.com/services/api/misc.urls.html
//                imageUrl = IMAGEPATH + imageServer + "/" + imageId + "_" + imageSecret + ".jpg";

                imageUrl = (String) (photoInfo.get("url_s"));
                Log.d(TAG, "Description: " + descriptionContent);
                Log.d(TAG, "title: " + titleContent);
                Log.d(TAG, "url: " + imageUrl);
            }

            // Actualizamos info en layout
            titleView.setText(!titleContent.isEmpty() ? titleContent : "No title");
            descriptionView.setText(!descriptionContent.isEmpty() ? descriptionContent : "No description");
            Picasso.get().load(imageUrl).into(imageView);

            // Hacemos que se cargue el mapa con los marcadores
            mapView.onCreate(new Bundle());
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Creando markers en el mapa");
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng userPosition = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        gmap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));

        // Ponemos el marker de la ubicación del usuario
        googleMap.addMarker(new MarkerOptions()
                .position(userPosition)
                .title("Mi posición"));
        // Ponemos el marker de la ubicación de la imagen
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(imgLat), Double.parseDouble(imgLon)))
                .title("Posición de la foto"));
    }
}