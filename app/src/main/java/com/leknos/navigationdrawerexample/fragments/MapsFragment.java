package com.leknos.navigationdrawerexample.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static final String APP_PREFERENCES_LATITUDE = "current_latitude";
    public static final String APP_PREFERENCES_LONGITUDE = "current_longitude";

    private double currentLatitude;
    private double currentLongitude;

    private boolean mLocationPermissionGranted;

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
                Toast.makeText(getContext(), "Moving to yours current position " + mLocationPermissionGranted, Toast.LENGTH_SHORT).show();
                cameraMoveToCurrentPosition();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());
    }

    public void cameraMoveToLastPosition() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    if (sharedPreferences.contains(APP_PREFERENCES_LATITUDE) && sharedPreferences.contains(APP_PREFERENCES_LONGITUDE)) {
                        currentLatitude = Double.parseDouble(sharedPreferences.getString(APP_PREFERENCES_LATITUDE, "0.0"));
                        currentLongitude = Double.parseDouble(sharedPreferences.getString(APP_PREFERENCES_LONGITUDE, "0.0"));
                        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(APP_PREFERENCES_LATITUDE, String.valueOf(currentLatitude));
                            editor.putString(APP_PREFERENCES_LONGITUDE, String.valueOf(currentLongitude));
                            editor.apply();
                        }
                    }
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 10);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });
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
                LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 10);
                mMap.animateCamera(cameraUpdate);
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney2"));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        //off visibility of default button by google maps
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //mMap.setOnMyLocationButtonClickListener(this);
        //mMap.setOnMyLocationClickListener(this);
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