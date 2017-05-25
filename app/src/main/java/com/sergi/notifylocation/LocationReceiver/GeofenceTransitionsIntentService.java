package com.sergi.notifylocation.LocationReceiver;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.sergi.notifylocation.LocationActivity;
import com.sergi.notifylocation.R;


public class GeofenceTransitionsIntentService extends IntentService {


    public GeofenceTransitionsIntentService() {
        super ("GeofenceTransitionsIntentService");
    }

    private final String TAG = "GeofenceIntentService";

    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "Error in geofencingEvent";
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Send notification and log the transition details.
            String placeName = intent.getStringExtra("name");
            double lat       = intent.getDoubleExtra("lat", 0);
            double longi     = intent.getDoubleExtra("long", 0);
            String address   = intent.getStringExtra("address");
            String phone     = intent.getStringExtra("phone");
            showNotification(placeName, lat, longi, address, phone);
        } else {
            // Log the error.
            Log.e(TAG, "Geofence transition not contempled");
        }
    }

    public void showNotification(String placeName, double lat, double longi,
                                 String address, String phone) {
        Intent resultIntent = new Intent(this, LocationActivity.class);

        resultIntent.putExtra("lat", lat);
        resultIntent.putExtra("long", longi);
        resultIntent.putExtra("name", placeName);
        resultIntent.putExtra("address", address);
        resultIntent.putExtra("phone", phone);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.place)
                        .setContentTitle("New place found")
                        .setContentText(placeName)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
        stopSelf();
    }
}
