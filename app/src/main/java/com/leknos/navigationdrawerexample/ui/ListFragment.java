package com.leknos.navigationdrawerexample.ui;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leknos.navigationdrawerexample.MainActivity;
import com.leknos.navigationdrawerexample.R;


public class ListFragment extends Fragment {
    // TODO: 01.08.2020 Добавить RecycleView и Retrofit 2 для парсинга GET запроса

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity activity = (MainActivity) getActivity();
        Toolbar toolbar = activity.getToolbar();
        toolbar.setTitle("TEST123");
        toolbar.setNavigationIcon(R.drawable.ic_mail);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }
}