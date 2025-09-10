package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.HomeHoriModel;

import java.util.List;

import adapters.HomeHoriAdapter;

public class HomeFragment extends Fragment {

    RecyclerView homeHorizontalRec;
    List<HomeHoriModel> homeHoriModelList;
    HomeHoriAdapter homeHoriAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        home
        return root;
    }
}