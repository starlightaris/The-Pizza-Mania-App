package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QuerySnapshot;
import com.nibm.pizzamaniamobileapp.model.Branch;
import com.nibm.pizzamaniamobileapp.repository.BranchRepository;

public class BranchViewModel extends ViewModel {
    private final BranchRepository repository;
    private final MutableLiveData<QuerySnapshot> branchesLiveData = new MutableLiveData<>();

    public BranchViewModel() {
        repository = new BranchRepository();
        repository.getBranches(branchesLiveData);
    }

    public LiveData<QuerySnapshot> getBranches() {
        return branchesLiveData;
    }

    public void addBranch(Branch branch) {
        repository.addBranch(branch);
    }

    public void updateBranch(Branch branch) {
        repository.updateBranch(branch);
    }

    public void deleteBranch(String branchId) {
        repository.deleteBranch(branchId);
    }
}
