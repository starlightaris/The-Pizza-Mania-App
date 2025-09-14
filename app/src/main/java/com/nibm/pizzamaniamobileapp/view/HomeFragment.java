package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.adapter.HomeHoriAdapter;
import com.nibm.pizzamaniamobileapp.adapter.HomeMenuAdapter;
import com.nibm.pizzamaniamobileapp.model.HomeHoriModel;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView homeHorizontalRec, homeMenuRec;
    private List<HomeHoriModel> homeHoriModelList;
    private HomeHoriAdapter homeHoriAdapter;

    private Spinner branchSpinner;
    private List<String> branchList;
    private List<String> branchIdList;

    private HomeMenuAdapter homeMenuAdapter;
    private MutableLiveData<List<MenuItem>> menuLiveData = new MutableLiveData<>();
    private MenuRepository repository = new MenuRepository();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // --- Branch Spinner ---
        branchSpinner = root.findViewById(R.id.branch_spinner);
        branchList = new ArrayList<>();
        branchIdList = new ArrayList<>();
        fetchBranchesFromFirestore();

        // --- Category Recycler ---
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
        homeHorizontalRec.setLayoutManager(
                new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false)
        );
        homeHorizontalRec.setHasFixedSize(true);
        homeHorizontalRec.setNestedScrollingEnabled(false);

        // --- Menu Recycler ---
        homeMenuRec = root.findViewById(R.id.home_menu_rec);
        homeMenuRec.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeMenuRec.setNestedScrollingEnabled(false);

        homeMenuAdapter = new HomeMenuAdapter(getActivity(), new ArrayList<>(), item -> {
            Log.d("HomeFragment", "Clicked menu item: " + item.getName());
        });
        homeMenuRec.setAdapter(homeMenuAdapter);

        menuLiveData.observe(getViewLifecycleOwner(), menuList -> homeMenuAdapter.updateList(menuList));

        return root;
    }

    private void fetchBranchesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("branches")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        branchList.clear();
                        branchIdList.clear();
                        for (var doc : task.getResult()) {
                            branchList.add(doc.getString("name"));
                            branchIdList.add(doc.getId());
                        }
                        setupBranchSpinner();
                    } else {
                        Log.e("HomeFragment", "Error fetching branches", task.getException());
                    }
                });
    }

    private void setupBranchSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_item, branchList); // selected item uses spinner_item
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // dropdown items
        branchSpinner.setAdapter(adapter);

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String branchId = branchIdList.get(position);
                fetchMenuItems(branchId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void fetchMenuItems(String branchId) {
        repository.getMenuItems(branchId, menuLiveData);
    }
}
