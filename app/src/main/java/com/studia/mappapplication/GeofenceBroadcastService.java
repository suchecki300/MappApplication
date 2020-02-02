package com.studia.mappapplication;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.util.Consumer;

import java.util.ArrayList;
import java.util.List;


public class GeofenceBroadcastService extends Service {

    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;
    private List<Shop> shopsss = new ArrayList<>();

    private static final long EXPIRATION_IN_HOURS = 12;
    static final long EXPIRATION_IN_MILLISECONDS = EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float RADIUS = 5000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        geofenceList = new ArrayList<>();
        geofencingClient = LocationServices.getGeofencingClient(this);
        populateGeofenceList();
    }

    private void populateGeofenceList() {
        FirestoreDatabaseService firestoreDatabaseService = new FirestoreDatabaseService();

        firestoreDatabaseService.getAllShops().forEach(shop -> geofenceList.add(new Geofence.Builder()
                .setRequestId(shop.getName())
                .setCircularRegion(
                        shop.getLatitude(),
                        shop.getLongitude(),
                        RADIUS
                )
                .setExpirationDuration(EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()));

        addGeofences();
    }

    private void addGeofences() {
        if (!geofenceList.isEmpty()) {
            startGeofencing();
        }
    }

    private void startGeofencing() {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Geofence added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Geofence not added" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
