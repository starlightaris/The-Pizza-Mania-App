package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.nibm.pizzamaniamobileapp.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public UserViewModel() {
        repository = new UserRepository();
    }
    public void register(String name, String email, String phone, String password) {
        repository.register(name, email, phone, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginSuccess.setValue(true);
                    } else {
                        if (task.getException() != null) {
                            errorMessage.setValue(task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Unknown error occurred");
                        }
                    }
                });
    }
    public void login(String email, String password) {
        repository.login(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loginSuccess.setValue(true);
                    } else {
                        if (task.getException() != null) {
                            errorMessage.setValue(task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Unknown error occurred");
                        }
                    }
                });
    }
    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
