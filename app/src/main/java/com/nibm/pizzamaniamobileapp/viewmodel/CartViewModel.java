package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.model.CartItem;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    private String selectedBranchId;
    private String userId;

    private PaymentMethod selectedPayment;

    public void setSelectedPayment(PaymentMethod payment) {
        this.selectedPayment = payment;
    }

    public PaymentMethod getSelectedPayment() {
        return selectedPayment;
    }


    // --- Cart Management ---
    public int addItem(MenuItem menuItem) {
        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null) items = new ArrayList<>();

        for (CartItem ci : items) {
            if (ci.getMenuId().equals(menuItem.getMenuId())) {
                ci.setQuantity(ci.getQuantity() + 1);
                cartItemsLiveData.setValue(items);
                return ci.getQuantity();
            }
        }

        CartItem newItem = new CartItem(menuItem.getMenuId(), menuItem.getName(),
                menuItem.getImageUrl(), menuItem.getPrice(), 1);
        items.add(newItem);
        cartItemsLiveData.setValue(items);
        return 1;
    }

    public int getItemCount(String menuId) {
        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null) return 0;
        for (CartItem ci : items) {
            if (ci.getMenuId().equals(menuId)) return ci.getQuantity();
        }
        return 0;
    }

    public void clearCart() {
        cartItemsLiveData.setValue(new ArrayList<>());
    }

    public double getTotalPrice() {
        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null) return 0.0;
        double total = 0.0;
        for (CartItem ci : items) {
            total += ci.getPrice() * ci.getQuantity();
        }
        return total;
    }

    // --- LiveData getters ---
    public LiveData<List<CartItem>> getCartItemsLiveData() {
        return cartItemsLiveData;
    }

    // --- Branch / User Info ---
    public String getSelectedBranchId() {
        return selectedBranchId != null ? selectedBranchId : "";
    }

    public void setSelectedBranchId(String selectedBranchId) {
        this.selectedBranchId = selectedBranchId;
    }

    public String getUserId() {
        return userId != null ? userId : "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Address Management

    private final MutableLiveData<Address> selectedAddress = new MutableLiveData<>();

    public LiveData<Address> getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address address) {
        selectedAddress.setValue(address);
    }

}
