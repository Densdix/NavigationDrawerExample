package com.leknos.taxiadmintask.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.leknos.taxiadmintask.R;
import com.leknos.taxiadmintask.utils.DistanceBetweenTwoPoints;

public class LocationInfoFragment extends Fragment {
    private TextView textView;
    private static final String TAG = "LocationInfoFragment";
    private BroadcastReceiver br;
    private FusedLocationProviderClient client;
    private double mLatitude;
    private double mLongitude;
    private double mSpeed;
    private double mDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_location_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textView = view.findViewById(R.id.fragment_location_info__text_view);
        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                mSpeed = location.getSpeed();

//                textView.setText("latitude = "+ mLatitude + "\n" +
//                        "longitude = "+mLongitude  + "\n" +
//                        "speed = "+mSpeed  + "\n" +
//                        "distance = "+mDistance+ " m.");
                textView.setText(getString(R.string.location_data_info, mLatitude, mLongitude, mSpeed, mDistance));
            }
        });


        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("GPSLocationUpdates")){
                    double la = intent.getExtras().getDouble("la");
                    double lo = intent.getExtras().getDouble("lo");
                    if (la != mLatitude && lo != mLongitude) {
                        mSpeed = intent.getExtras().getDouble("speed");
                        mDistance = DistanceBetweenTwoPoints.getDistance(mLatitude, mLongitude, la, lo);
                        mLatitude = la;
                        mLongitude = lo;
                        //textView.setText(getString(R.string.location_data_info, la, lo, mSpeed, mDistance));
                        textView.setText("latitude = "+ String.format("%.2f",mLatitude) + "\n" +
                        "longitude = "+String.format("%.2f", mLongitude)  + "\n" +
                        "speed = "+String.format("%.2f", mSpeed)  + " m/s\n" +
                        "distance = "+String.format("%.2f", mDistance)+ " m.");

                        Log.d(TAG, "onReceived!: "+la+lo);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(br, new IntentFilter("GPSLocationUpdates"));
        super.onViewCreated(view, savedInstanceState);
    }
}