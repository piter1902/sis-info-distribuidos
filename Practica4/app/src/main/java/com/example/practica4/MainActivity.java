package com.example.practica4;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private MyAsyncTask myTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Implementar funcionalidad de búsqueda", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
// Para obtener el valor devuelto en onRetainCustomNonConfigurationInstance
        myTask = (MyAsyncTask) getLastCustomNonConfigurationInstance();
        if (myTask == null) {
// Evita crear una AsyncTask cada vez que, por ejemplo, hay una rotación
            Log.i(TAG, "onCreate: About to create MyAsyncTask");
            myTask = new MyAsyncTask(this);
            // Hacemos la petición a la API
            myTask.execute("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=e45df953d132e8de1fac638da4ed55bc&format=json");
        } else
            myTask.attach(this);
        Toast.makeText(this, "Hola!", Toast.LENGTH_LONG).show();
    }

    /**
     * Permite devolver un objeto y que persista entre cambios de configuración. Lo
     * invoca el sistema cuando se va a destruir una actividad y se sabe que se va a
     * crear otra nueva inmediatamente. Se llama entre onStop y onDestroy.
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
// Además de devolver mi tarea, elimino la referencia en mActivity
        myTask.detach();
// Devuelvo mi tarea, para que no se cree de nuevo cada vez
        return myTask;
    }

    public void setupAdapter(Integer integer) {
        if (integer != -1)
            Toast.makeText(MainActivity.this,
                    "Codigo de respuesta: " + integer, Toast.LENGTH_LONG).show();
    }
}

/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
}
*/