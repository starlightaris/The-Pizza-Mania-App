package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nibm.pizzamaniamobileapp.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> roleLiveData = new MutableLiveData<>();
    public UserViewModel() {
        repository = new UserRepository();
    }
    public void register(String name, String email, String phone, String password) {
        repository.register(name, email, phone, password, "customer")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        String uid = task.getResult().getUser().getUid();
                        repository.fetchUserRole(uid, roleLiveData);
                    } else {
                        if (task.getException() != null) {
                            errorMessage.setValue(task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Unknown error occurred during registration");
                        }
                    }
                });
    }
    public void login(String email, String password) {
        repository.login(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        String uid = task.getResult().getUser().getUid();
                        repository.fetchUserRole(uid, roleLiveData);
                    } else {
                        if (task.getException() != null) {
                            errorMessage.setValue(task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Unknown error occurred during login");
                        }
                    }
                });
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<String> getRoleLiveData() {
        return roleLiveData;
    }
}
