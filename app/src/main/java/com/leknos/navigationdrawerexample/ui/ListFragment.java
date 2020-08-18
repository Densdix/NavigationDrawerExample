package com.leknos.navigationdrawerexample.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leknos.navigationdrawerexample.MainActivity;
import com.leknos.navigationdrawerexample.PhotosListAdapter;
import com.leknos.navigationdrawerexample.R;
import com.leknos.navigationdrawerexample.model.Photo;
import com.leknos.navigationdrawerexample.utils.NetworkService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PhotosListAdapter mAdapter;

    // TODO: 01.08.2020 Добавить RecycleView и Retrofit 2 для парсинга GET запроса

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity activity = (MainActivity) getActivity();
        Toolbar toolbar = activity.getToolbar();
        //toolbar.setTitle("TEST123");
        //toolbar.setNavigationIcon(R.drawable.ic_mail);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_list__recycler_view);
        postRequest();

    }

    public void postRequest() {
        Call<List<Photo>> call = NetworkService.getInstance().getJSONApi().getPhotos();
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if (!response.isSuccessful()) {
                    Log.e("APP_FAILURE", String.valueOf(response.code()));
                    return;
                }
                ArrayList<Photo> photos = (ArrayList<Photo>) response.body();
                recyclerView.setHasFixedSize(true);
                mAdapter = new PhotosListAdapter(photos);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {

            }
        });
    }
}