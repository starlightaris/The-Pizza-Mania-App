package com.nibm.pizzamaniamobileapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nibm.pizzamaniamobileapp.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    private final FirebaseFirestore db;

    public MenuRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addMenuItem(String branchId, MenuItem item) {
        var ref = db.collection("branches")
                .document(branchId)
                .collection("menu")
                .document();
        item.setMenuId(ref.getId());
        ref.set(item)
                .addOnSuccessListener(unused -> Log.d("Firestore", "✅ Menu item added: " + item.getName()))
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Failed to add menu item", e));
    }

    public void updateMenuItem(String branchId, MenuItem item) {
        if (item.getMenuId() == null || item.getMenuId().isEmpty()) {
            Log.e("Firestore", "❌ Cannot update menu item without ID");
            return;
        }

        db.collection("branches")
                .document(branchId)
                .collection("menu")
                .document(item.getMenuId())
                .set(item)
                .addOnSuccessListener(unused -> Log.d("Firestore", "✅ Menu item updated: " + item.getName()))
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Failed to update menu item", e));
    }

    public void deleteMenuItem(String branchId, String menuId) {
        db.collection("branches")
                .document(branchId)
                .collection("menu")
                .document(menuId)
                .delete()
                .addOnSuccessListener(unused -> Log.d("Firestore", "✅ Menu item deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Failed to delete menu item", e));
    }

    public void getMenuItems(String branchId, MutableLiveData<List<MenuItem>> menuItemsLiveData) {
        db.collection("branches")
                .document(branchId)
                .collection("menu")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "❌ Listen failed", error);
                        return;
                    }

                    if (value != null) {
                        List<MenuItem> items = new ArrayList<>();
                        for (var doc : value.getDocuments()) {
                            MenuItem item = doc.toObject(MenuItem.class);
                            if (item != null) {
                                item.setMenuId(doc.getId());
                                items.add(item);
                            }
                        }
                        menuItemsLiveData.setValue(items);
                    }
                });
    }
}
