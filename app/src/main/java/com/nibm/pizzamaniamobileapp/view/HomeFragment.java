package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.HomeHoriModel;

import java.util.ArrayList;
import java.util.List;

import adapters.HomeHoriAdapter;

public class HomeFragment extends Fragment {

    RecyclerView homeHorizontalRec;
    List<HomeHoriModel> homeHoriModelList;
    HomeHoriAdapter homeHoriAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeHorizontalRec = root.findViewById(R.id.home_hori_rec);

        homeHoriModelList = new ArrayList<>();

        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_pizza, "Pizza"));
        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_burgers, "Burgers"));
        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_fries, "Sides"));
        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_beverages, "Drinks"));
        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_icecream, "Dessert"));
        homeHoriModelList.add(new HomeHoriModel(R.drawable.icon_cupcake, "Cake"));

        homeHoriAdapter = new HomeHoriAdapter(getActivity(), homeHoriModelList);
        homeHorizontalRec.setAdapter(homeHoriAdapter);
        homeHorizontalRec.setLayoutManager((new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL, false)));
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);
        return root;
    }
}