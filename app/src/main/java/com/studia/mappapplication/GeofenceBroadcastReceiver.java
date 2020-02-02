package com.studia.mappapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String ENTER_TEXT = "Witaj w ";
    private static final String EXIT_TEXT = "Wyszedles z ";
    private static final String UNKNOWN_TEXT = "Unknown geofence transition";
    private static final String CHANNEL_ID = "channel_01";
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private int id = 123;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
            sendNotification(geofenceTransitionDetails);
        } else {
            Log.e(TAG, UNKNOWN_TEXT);
        }
    }

    private String getCorrectStrings(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return ENTER_TEXT;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return EXIT_TEXT;
            default:
                return UNKNOWN_TEXT;
        }
    }


    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String geofenceTransitionName = getCorrectStrings(geofenceTransition);
        StringBuilder triggeringGeofencesDetails = new StringBuilder(geofenceTransitionName);
        triggeringGeofences.forEach(geofence -> triggeringGeofencesDetails.append(" " + geofence.getRequestId()));

        return triggeringGeofencesDetails.toString();
    }


    private void sendNotification(String notificationDetails) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(notificationDetails)
                .setContentText(notificationDetails)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                "notification", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(id++, notification.build());
    }
}