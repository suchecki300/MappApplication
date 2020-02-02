package com.studia.mappapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private Button optionsListButton;
    private Button logoutButton;
    private Button googleMapsButton;
    private Button shopsListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getElements();
        registerButtonListeners();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 44);
        } else {
            startGeofenceBroadcastService();
        }

    }

    private void startGeofenceBroadcastService() {
        Log.d("zadzialal geofence broadcast", "broadcast start");
        startService(new Intent(MainActivity.this, GeofenceBroadcastService.class));
    }


    private void getElements() {
        optionsListButton = findViewById(R.id.options_button);
        logoutButton = findViewById(R.id.logout_button);
        googleMapsButton = findViewById(R.id.google_map_button);
        shopsListButton = findViewById(R.id.shops_button);
    }

    private void registerButtonListeners() {

        optionsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOptionsActivity(v);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(v);
            }
        });

        googleMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMapsActivity(v);
            }
        });

        shopsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShopsListActivity(v);
            }
        });
    }



    public void logout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToOptionsActivity(View view) {
        Intent intent = new Intent(this, ColorOptionsActivity.class);
        startActivity(intent);
    }

    public void goToGoogleMapsActivity(View view) {
        Intent intent = new Intent(this, GoogleMapsActivity.class);
        startActivity(intent);
    }

    public void goToShopsListActivity(View view) {
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Brak zezwolen", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                startGeofenceBroadcastService();
            }
        }
    }
}