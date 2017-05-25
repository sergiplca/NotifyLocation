package com.sergi.notifylocation.LocationReceiver;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.sergi.notifylocation.PermissionsActivity;
import com.sergi.notifylocation.R;

import java.util.Set;

public class LocationService extends    Service
                             implements GoogleApiClient.ConnectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        LocationListener,
                                        ResultCallback
{

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 20000; // 20 sec
    private static int FATEST_INTERVAL = 10000; // 10 sec
    private static int DISPLACEMENT = 20; // 10 meters

    private static final int COARSE = 1;
    private static final int FINE = 2;


    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private boolean mRequestingLocationUpdates = false;

    private Geofence geoFence;
    private GeofencingRequest geofencingRequest;
    private PendingIntent mGeofencePendingIntent;

    SharedPreferences prefs;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startid) {
        Toast.makeText(this, "Service initiated", Toast.LENGTH_LONG).show();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        return startid;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onConnected(Bundle arg0) {
        Log.d("Connected", "Connected");
        // Once connected with google api, get the location
        mRequestingLocationUpdates = true;
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showNotification(FINE);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                showNotification(COARSE);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                Log.d("LocationUpdates", "Location Updates started");
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Error", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location Changed", "Location Changed");
        callPlaceDetectionApi();
    }

    private void callPlaceDetectionApi() throws SecurityException {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                for (PlaceLikelihood pL : likelyPlaces) {
                    if (checkPlaceInPreferences(pL.getPlace())) {
                        buildGeofence(pL.getPlace());
                        break;
                    }
                }
                likelyPlaces.release();

                return;
            }
        });
    }

    private void buildGeofence (Place place) throws SecurityException {

        addGeofence(place);
        geofencingRequest = getGeofencingRequest();
        mGeofencePendingIntent = getGeofencePendingIntent(place);

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                geofencingRequest,
                mGeofencePendingIntent
        ).setResultCallback(this);
    }

    private void addGeofence (Place place) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer radius;

        if (!TextUtils.isEmpty(prefs.getString("radius", "")))
            radius = Integer.parseInt(prefs.getString("radius", ""));
        else
            radius = 50;

        LatLng ll = place.getLatLng();

        geoFence = new Geofence.Builder()
                .setRequestId(place.getName().toString())
                .setCircularRegion(
                        ll.latitude,
                        ll.longitude,
                        radius)
                .setExpirationDuration(5000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geoFence);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent(Place place) {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        intent.putExtra("name", place.getName().toString());
        intent.putExtra("lat", place.getLatLng().latitude);
        intent.putExtra("long", place.getLatLng().longitude);
        intent.putExtra("address", place.getAddress());
        intent.putExtra("phone", place.getPhoneNumber());

        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    public boolean checkPlaceInPreferences(Place place) {

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selections = prefs.getStringSet("locations_available", null);

        if (selections == null)
            return false;

        for (Integer type : place.getPlaceTypes()) {
            if (selections.contains(String.valueOf(type))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onResult(@NonNull Result result) {
    }

    public void showNotification(int permission) {
        Intent resultIntent = new Intent(this, PermissionsActivity.class);
        resultIntent.putExtra("value", permission);

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
                        .setContentTitle("Location Services need permission")
                        .setContentText("Click to open")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }
}
