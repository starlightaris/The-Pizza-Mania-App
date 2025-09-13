package com.nibm.pizzamaniamobileapp.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nibm.pizzamaniamobileapp.model.Branch;

public class BranchRepository {
    private final FirebaseFirestore db;
    public BranchRepository() {
        db = FirebaseFirestore.getInstance();
    }
    public void addBranch(Branch branch) {
        db.collection("branches")
                .add(branch)  // Firestore generates unique ID
                .addOnSuccessListener(ref -> {
                    // Save the generated ID into the branch object
                    String id = ref.getId();
                    branch.setBranchId(id);

                    // Update the document with its ID field
                    ref.update("branchId", id)
                            .addOnSuccessListener(unused ->
                                    Log.d("Firestore", "✅ Branch added with ID: " + id))
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "❌ Failed to update branchId", e));
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "❌ Failed to add branch", e));
    }
    public void updateBranch(Branch branch) {
        db.collection("branches").document(branch.getBranchId())
                .update(
                        "name", branch.getName(),
                        "location", branch.getLocation(),
                        "contact", branch.getContact()
                )
                .addOnSuccessListener(unused -> Log.d("Firestore", "✅ Branch updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "❌ Failed to update branch", e));
    }
    public void deleteBranch(String branchId) {
        db.collection("branches").document(branchId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        doc.getReference().delete()
                                .addOnSuccessListener(unused ->
                                        Log.d("Firestore", "✅ Branch deleted: " + branchId))
                                .addOnFailureListener(e ->
                                        Log.e("Firestore", "❌ Failed to delete branch: " + branchId, e));
                    } else {
                        Log.w("Firestore", "⚠️ Branch not found: " + branchId);
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "❌ Error checking branch before delete", e));
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
}
