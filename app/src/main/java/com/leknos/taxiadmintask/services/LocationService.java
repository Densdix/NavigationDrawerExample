package com.leknos.taxiadmintask.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private LocationRequest locationRequest;
    LocationCallback mLocationCallback;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;

    public String szSatellitesInUse, szSatellitesInView;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    private static final long MIN_TIME_BW_UPDATES = 1000; //1 second

    private GpsStatus mGpsStatus;
    protected GpsListener gpsListener = new GpsListener();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(location != null){
                    double la = location.getLatitude();
                    double lo = location.getLongitude();
                    double speed = location.getSpeed();
                    saveLocation(la, lo, speed);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("TaxiAdminTask")
                    .setContentText("updating location every 4 seconds")
                    .build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return START_NOT_STICKY;


    }

    private void getLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.addGpsStatusListener(gpsListener);
        locationManager.getGpsStatus(null);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        mGpsStatus = locationManager.getGpsStatus(mGpsStatus);
        Iterable<GpsSatellite> satellites = mGpsStatus.getSatellites();
        int iTempCountInView = 0;
        int iTempCountInUse = 0;
        if (satellites != null) {
            for (GpsSatellite gpsSatellite : satellites) {
                iTempCountInView++;
                if (gpsSatellite.usedInFix()) {
                    iTempCountInUse++;
                }
            }
        }
        szSatellitesInView = String.valueOf(iTempCountInView);
        szSatellitesInUse = String.valueOf(iTempCountInUse);
        Log.d(TAG, "szSatellitesInView: "+ szSatellitesInView);
        Log.d(TAG, "szSatellitesInUse: "+ szSatellitesInView);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }



    private void saveLocation(double la, double lo, double speed) {
            Intent i = new Intent("GPSLocationUpdates");
            i.putExtra("la", la);
            Log.d(TAG, "saveLocation: "+la);
            i.putExtra("lo", lo);
            i.putExtra("speed", speed);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private class GpsListener implements GpsStatus.Listener{

        @Override
        public void onGpsStatusChanged(int event) {

        }
    }


    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }
}
