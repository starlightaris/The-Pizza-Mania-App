package com.nibm.pizzamaniamobileapp.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nibm.pizzamaniamobileapp.model.Branch;
import com.nibm.pizzamaniamobileapp.utils.App; // A helper class for global context

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchRepository {
    private final FirebaseFirestore db;
    public BranchRepository() {
        db = FirebaseFirestore.getInstance();
    }
    public void addBranch(Branch branch) {
        db.collection("branches")
                .add(branch)  // Firestore generates unique ID
                .addOnSuccessListener(ref -> {
                    String id = ref.getId();
                    branch.setBranchId(id);

                    // Update document with its ID field
                    ref.update("branchId", id)
                            .addOnSuccessListener(unused -> {
                                Log.d("Firestore", "✅ Branch added with ID: " + id);
                                Toast.makeText(App.getContext(), "Branch added", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "❌ Failed to update branchId", e);
                                Toast.makeText(App.getContext(), "Failed to update branch ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Failed to add branch", e);
                    Toast.makeText(App.getContext(), "Failed to add branch", Toast.LENGTH_SHORT).show();
                });
    }
    public void updateBranch(Branch branch) {
        db.collection("branches").document(branch.getBranchId())
                .update(
                        "name", branch.getName(),
                        "location", branch.getLocation(),
                        "contact", branch.getContact()
                )
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", "✅ Branch updated");
                    Toast.makeText(App.getContext(), "Branch updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Failed to update branch", e);
                    Toast.makeText(App.getContext(), "Failed to update branch", Toast.LENGTH_SHORT).show();
                });
    }
    public void deleteBranch(String branchId) {
        db.collection("branches").document(branchId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        doc.getReference().delete()
                                .addOnSuccessListener(unused -> {
                                    Log.d("Firestore", "✅ Branch deleted: " + branchId);
                                    Toast.makeText(App.getContext(), "Branch deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "❌ Failed to delete branch: " + branchId, e);
                                    Toast.makeText(App.getContext(), "Failed to delete branch", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.w("Firestore", "⚠️ Branch not found: " + branchId);
                        Toast.makeText(App.getContext(), "Branch not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Error checking branch before delete", e);
                    Toast.makeText(App.getContext(), "Error deleting branch", Toast.LENGTH_SHORT).show();
                });
    }
    public void getBranches(MutableLiveData<QuerySnapshot> branchesLiveData) {
        db.collection("branches")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "❌ Listen failed", error);
                        return;
                    }
                    if (value != null) {
                        branchesLiveData.setValue(value);
                    }
                });
    }
    public void getAllBranchesForDropdown(MutableLiveData<List<Map<String, String>>> branchesLiveData) {
        db.collection("branches")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, String>> branchList = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Map<String, String> branchMap = new HashMap<>();
                        branchMap.put("branchId", doc.getId());
                        branchMap.put("name", doc.getString("name"));
                        branchList.add(branchMap);
                    }
                    branchesLiveData.setValue(branchList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Failed to fetch branches for dropdown", e);
                    branchesLiveData.setValue(new ArrayList<>());
                });
    }
}
