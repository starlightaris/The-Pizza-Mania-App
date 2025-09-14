package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QuerySnapshot;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.repository.MenuRepository;

import java.util.ArrayList;
import java.util.List;

public class MenuViewModel extends ViewModel {

    private final MenuRepository repository;
    private final MutableLiveData<List<MenuItem>> menuItemsLiveData;
    private final MutableLiveData<QuerySnapshot> firestoreLiveData;

    private String currentBranchId;

    public MenuViewModel() {
        repository = new MenuRepository();
        menuItemsLiveData = new MutableLiveData<>(new ArrayList<>());
        firestoreLiveData = new MutableLiveData<>();

        // Observe Firestore snapshots and convert to MenuItem list
        firestoreLiveData.observeForever(querySnapshot -> {
            if (querySnapshot != null) {
                List<MenuItem> items = new ArrayList<>();
                for (var doc : querySnapshot.getDocuments()) {
                    MenuItem item = doc.toObject(MenuItem.class);
                    if (item != null) items.add(item);
                }
                menuItemsLiveData.setValue(items);
            }
        });
    }

    public LiveData<List<MenuItem>> getMenuItemsLiveData() {
        return menuItemsLiveData;
    }

    public void setBranchId(String branchId) {
        currentBranchId = branchId;
        fetchMenuItems();
    }

    public void fetchMenuItems() {
        if (currentBranchId != null) {
            repository.getMenuItems(currentBranchId, menuItemsLiveData);
        }
    }

    public void addMenuItem(MenuItem item) {
        if (currentBranchId != null) {
            repository.addMenuItem(currentBranchId, item);
        }
    }

    public void updateMenuItem(MenuItem item) {
        if (currentBranchId != null) {
            repository.updateMenuItem(currentBranchId, item);
        }
    }

    public void deleteMenuItem(String menuId) {
        if (currentBranchId != null) {
            repository.deleteMenuItem(currentBranchId, menuId);
        }
    }
}
