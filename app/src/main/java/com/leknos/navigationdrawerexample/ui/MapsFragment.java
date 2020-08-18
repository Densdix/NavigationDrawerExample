package com.leknos.navigationdrawerexample.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leknos.navigationdrawerexample.MainActivity;
import com.leknos.navigationdrawerexample.R;

public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    SharedPreferences sharedPreferences;

    private GoogleMap mMap;
    private FusedLocationProviderClient client;

    public static final String APP_PREFERENCES_LATITUDE = "current_latitude";
    public static final String APP_PREFERENCES_LONGITUDE = "current_longitude";
    public static final int CAMERA_ZOOM = 15;

    private double currentLatitude;
    private double currentLongitude;

    private BroadcastReceiver br;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    private FloatingActionButton floatingActionButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        floatingActionButton = getView().findViewById(R.id.fragment_maps__fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Moving to yours current position", Toast.LENGTH_SHORT).show();
                cameraMoveToCurrentPosition();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("GPSLocationUpdates")){
                    double la = intent.getExtras().getDouble("la");
                    double lo = intent.getExtras().getDouble("lo");
                    updateCamera(la, lo);
                }

            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(br, new IntentFilter("GPSLocationUpdates"));
    }

    public void cameraMoveToLastPosition() {
        if (sharedPreferences.contains(APP_PREFERENCES_LATITUDE) && sharedPreferences.contains(APP_PREFERENCES_LONGITUDE)) {
            currentLatitude = Double.parseDouble(sharedPreferences.getString(APP_PREFERENCES_LATITUDE, "0.0"));
            currentLongitude = Double.parseDouble(sharedPreferences.getString(APP_PREFERENCES_LONGITUDE, "0.0"));
            if (currentLatitude == 0.0 && currentLongitude == 0.0) {
                //default for firs app run
                //coords of Nikolaev 46.9762, 31.9975
                currentLatitude = 46.9762;
                currentLongitude = 31.9975;
            }
        }
        updateCamera(currentLatitude, currentLongitude);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Last Position"));
    }

    public void cameraMoveToCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                updateCamera(currentLatitude, currentLongitude);
            }
        });
    }

    public void updateCamera(double currentLatitude, double currentLongitude){
        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, CAMERA_ZOOM);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        String locationInfo = "Current location:\n" +
                "latitude = "+location.getLatitude()+"\n"+
                "longitude = "+location.getLongitude()+"\n"+
                "speed = "+location.getSpeed()+"\n"+
                "time = "+location.getTime()+"\n";
        Toast.makeText(getContext(), locationInfo + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney2"));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        //off visibility of default button by google maps
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        cameraMoveToLastPosition();
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onStop() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_PREFERENCES_LATITUDE, String.valueOf(currentLatitude));
        editor.putString(APP_PREFERENCES_LONGITUDE, String.valueOf(currentLongitude));
        editor.apply();
        super.onStop();
    }
}