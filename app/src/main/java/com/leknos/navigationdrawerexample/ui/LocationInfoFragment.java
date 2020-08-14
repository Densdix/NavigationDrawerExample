package com.leknos.navigationdrawerexample.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leknos.navigationdrawerexample.R;

public class LocationInfoFragment extends Fragment {
    private TextView textView;
    private static final String TAG = "LocationInfoFragment";
    private BroadcastReceiver br;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_location_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textView = view.findViewById(R.id.fragment_location_info__text_view);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("GPSLocationUpdates")){
                    double la = intent.getExtras().getDouble("la");
                    double lo = intent.getExtras().getDouble("lo");
                    textView.setText("la = "+ la + "\n" +
                            "lo = "+lo);
                    Log.d(TAG, "onReceived!: "+la+lo);
                }

            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(br, new IntentFilter("GPSLocationUpdates"));
        super.onViewCreated(view, savedInstanceState);
    }
}