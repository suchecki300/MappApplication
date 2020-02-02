package com.studia.mappapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Strings;

import java.util.UUID;


public class AddShopActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String RANGE = "range";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private Button addShopButton;
    private Button getCurrentGeolocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shop_activity);
        getElements();
        registerButtonsListeners();
        saveShopDataFromGoogleMapIntent();
    }

    private void getElements() {
        addShopButton = findViewById(R.id.submit_add_shop);
        getCurrentGeolocationButton = findViewById(R.id.current_location_button);

    }

    private void registerButtonsListeners() {
        registerSubmitAddShopButtonListener();
        registerGetCurrentGeolocationButtonListener();
    }

    private void registerSubmitAddShopButtonListener() {
        addShopButton.setOnClickListener(view -> {
            Shop shop = retrieveShopFromForm();
            addNewShop(shop);
            navigateToShopListActivity();
        });
    }

    private void registerGetCurrentGeolocationButtonListener() {
        getCurrentGeolocationButton.setOnClickListener(view -> {
            GeolocationPermissionService.requestPermissionForLocation(this);
            setCurrentGeolocationDetails();
        });
    }



    private Shop retrieveShopFromForm() {
        String id = UUID.randomUUID().toString();
        String name = getNameFieldValue();
        String description = getDescriptionFieldValue();
        double range = Double.parseDouble(getRangeFieldValue());
        double latitude = Double.parseDouble(getLatitudeFieldValue());
        double longitude = Double.parseDouble(getLongitudeFieldValue());

        return new Shop(id, name, description, range, latitude, longitude);
    }

    private void addNewShop(Shop shop) {
        FirestoreDatabaseService databaseService = new FirestoreDatabaseService();
        databaseService.insertShop(shop);
    }

    private void saveShopDataFromGoogleMapIntent() {
        Intent intent = getIntent();

        TextView nameField = findViewById(R.id.shop_name_add_text);
        nameField.setText(Strings.nullToEmpty(intent.getStringExtra(NAME)));
        TextView descriptionField = findViewById(R.id.shop_description_add_text);
        descriptionField.setText(Strings.nullToEmpty(intent.getStringExtra(DESCRIPTION)));
        TextView rangeField = findViewById(R.id.shop_range_add_text);
        rangeField.setText(Strings.nullToEmpty(intent.getStringExtra(RANGE)));
        TextView latitudeField = findViewById(R.id.shop_latitude_add_text);
        latitudeField.setText(Strings.nullToEmpty(intent.getStringExtra(LATITUDE)));
        TextView longitudeField = findViewById(R.id.shop_longitude_add_text);
        longitudeField.setText(Strings.nullToEmpty(intent.getStringExtra(LONGITUDE)));
    }

    public void setCurrentGeolocationDetails() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        TextView latitudeField = findViewById(R.id.shop_latitude_add_text);
                        TextView longitudeField = findViewById(R.id.shop_longitude_add_text);
                        latitudeField.setText(String.valueOf(location.getLatitude()));
                        longitudeField.setText(String.valueOf(location.getLongitude()));
                    }
                });
    }

    private void navigateToShopListActivity() {
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    private String getNameFieldValue() {
        TextView nameField = findViewById(R.id.shop_name_add_text);
        return Strings.nullToEmpty(String.valueOf(nameField.getText()));
    }

    private String getDescriptionFieldValue() {
        TextView descriptionField = findViewById(R.id.shop_description_add_text);
        return Strings.nullToEmpty(String.valueOf(descriptionField.getText()));
    }

    private String getRangeFieldValue() {
        TextView rangeField = findViewById(R.id.shop_range_add_text);
        return Strings.nullToEmpty(String.valueOf(rangeField.getText()));
    }

    private String getLatitudeFieldValue() {
        TextView latitudeField = findViewById(R.id.shop_latitude_add_text);
        return Strings.nullToEmpty(String.valueOf(latitudeField.getText()));
    }

    private String getLongitudeFieldValue() {
        TextView longitudeField = findViewById(R.id.shop_longitude_add_text);
        return Strings.nullToEmpty(String.valueOf(longitudeField.getText()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GeolocationPermissionService.PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Brak dostepu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                setCurrentGeolocationDetails();
            }
        }
    }
}