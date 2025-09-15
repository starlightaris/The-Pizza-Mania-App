package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nibm.pizzamaniamobileapp.model.Address;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> userName = new MutableLiveData<>("Jonny boi");
    private MutableLiveData<String> email = new MutableLiveData<>("john@example.com");
    private MutableLiveData<String> phone = new MutableLiveData<>("");
    private final MutableLiveData<List<Address>> addressListLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getPhone() { return phone; }

    public LiveData<List<Address>> getAddressListLiveData() {
        return addressListLiveData;
    }

    //Edit Profile
    public void updateUser(String name, String emailAddress,  String phoneNum) {
        userName.setValue(name);
        email.setValue(emailAddress);
        phone.setValue(phoneNum);
    }

    //Address
    public void addAddress(Address address) {
        List<Address> current = addressListLiveData.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(address);
        addressListLiveData.setValue(current);
    }
    public void updateAddress(Address updated) {
        List<Address> current = addressListLiveData.getValue();
        if (current == null) return;
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i).getAddressId().equals(updated.getAddressId())) {
                current.set(i, updated);
                break;
            }
        }
        addressListLiveData.setValue(current);
    }
    public void deleteAddress(Address toDelete) {
        List<Address> current = addressListLiveData.getValue();
        if (current == null) return;
        current.remove(toDelete);
        addressListLiveData.setValue(current);
    }
}
