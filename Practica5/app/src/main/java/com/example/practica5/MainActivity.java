package com.example.practica5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Looper;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final String TAG = "MainActivity";

    // Creamos cliente de google
        private FusedLocationProviderClient fusedLocationClient;
    private GoogleApiClient googleApiClient;

    private Double latit;
    private Double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Pulso botón");
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setNumUpdates(1);
                locationRequest.setInterval(0);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d(TAG, "No tenemos permisos");
                    return;
                }

                fusedLocationClient.requestLocationUpdates(locationRequest,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult result) {
                                Log.d(TAG, "onLocationResult: Inicio");
                                if (result == null)
                                    return;
                                int lastIndex = result.getLocations().size() - 1;

                                Location location = result.getLocations().get(lastIndex);

                                // Actualizamos los valores de lat y lon
                                latit = location.getLatitude();
                                longi = location.getLongitude();
                                Log.d(TAG, "Lat actualizada: " + latit + " | Long actualizada: " + longi);

                                // Create new Intent to change Activity
                                Intent intent = new Intent(getApplicationContext(), DetailPhotoActivity.class);
                                // Bundle to pass info to new Activity
                                intent.putExtra("lat", latit.toString());
                                intent.putExtra("lon", longi.toString());

                                startActivity(intent);

                            }
                        },
                        Looper.getMainLooper()
                );
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "No tenemos permisos");
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latit = location.getLatitude();
                            longi = location.getLongitude();
                            Log.d(TAG, "Lat: " + latit + " | Long: " + longi);
                        } else {

                        }
                    }
                }
        );
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    // Rutina que se ejecuta al aceptar
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "No se aceptó permisos", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}