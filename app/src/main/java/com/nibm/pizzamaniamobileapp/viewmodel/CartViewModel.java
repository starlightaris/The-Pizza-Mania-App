package com.nibm.pizzamaniamobileapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nibm.pizzamaniamobileapp.model.Address;
import com.nibm.pizzamaniamobileapp.model.CartItem;
import com.nibm.pizzamaniamobileapp.model.MenuItem;
import com.nibm.pizzamaniamobileapp.model.PaymentMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    private String selectedBranchId;
    private String userId;

    private PaymentMethod selectedPayment;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // Check if item already exists in cart
        for (CartItem ci : items) {
            if (ci.getMenuId().equals(menuItem.getMenuId())) {
                ci.setQuantity(ci.getQuantity() + 1);
                cartItemsLiveData.setValue(items);
                saveCartToFirestore(userId);
                return ci.getQuantity();
            }
        }

        // Add new item to cart
        CartItem newItem = new CartItem(menuItem.getMenuId(), menuItem.getName(),
                menuItem.getImageUrl(), menuItem.getPrice(), 1);
        items.add(newItem);
        cartItemsLiveData.setValue(items);
        saveCartToFirestore(userId);
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
        if (userId != null && !userId.isEmpty()) {
            // Delete all cart items from Firestore
            db.collection("users").document(userId)
                    .collection("cartItems")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                    });
        }
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

    // Firestore Cart Persistence
    public void saveCartToFirestore(String userId) {
        if (userId == null || userId.isEmpty()) {
            System.out.println("DEBUG: Cannot save cart - userId is null or empty");
            return;
        }

        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null || items.isEmpty()) {
            System.out.println("DEBUG: Cart is empty, clearing Firestore cart");
            clearCart();
            return;
        }

        System.out.println("DEBUG: Saving " + items.size() + " items to Firestore for user: " + userId);

        // First, get existing cart items to know what to update/delete
        db.collection("users").document(userId)
                .collection("cartItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, CartItem> currentItemsMap = new HashMap<>();
                    for (CartItem item : items) {
                        currentItemsMap.put(item.getMenuId(), item);
                    }

                    // Update or create cart items
                    for (CartItem item : items) {
                        // Check if this item already exists in Firestore
                        boolean itemExists = false;
                        String existingDocId = null;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String existingMenuId = document.getString("menuId");
                            if (existingMenuId != null && existingMenuId.equals(item.getMenuId())) {
                                itemExists = true;
                                existingDocId = document.getId();
                                break;
                            }
                        }

                        Map<String, Object> itemData = new HashMap<>();
                        itemData.put("menuId", item.getMenuId());
                        itemData.put("name", item.getName());
                        itemData.put("imageUrl", item.getImageUrl());
                        itemData.put("price", item.getPrice());
                        itemData.put("quantity", item.getQuantity());
                        itemData.put("lastUpdated", FieldValue.serverTimestamp());

                        if (itemExists && existingDocId != null) {
                            // Update existing document - itemId remains the same (auto-generated doc ID)
                            db.collection("users").document(userId)
                                    .collection("cartItems")
                                    .document(existingDocId)
                                    .set(itemData)
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("DEBUG: Successfully updated item: " + item.getMenuId());
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("DEBUG: Failed to update item " + item.getMenuId() + ": " + e.getMessage());
                                    });
                        } else {
                            // Create new document with auto-generated ID
                            var docRef = db.collection("users").document(userId)
                                    .collection("cartItems")
                                    .document();

                            // Store the auto-generated document ID in the itemId field
                            itemData.put("itemId", docRef.getId());

                            docRef.set(itemData)
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("DEBUG: Successfully created item: " + item.getMenuId() + " with docId: " + docRef.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("DEBUG: Failed to create item " + item.getMenuId() + ": " + e.getMessage());
                                    });
                        }
                    }

                    // Delete items that are no longer in the cart
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String menuId = document.getString("menuId");
                        if (menuId != null && !currentItemsMap.containsKey(menuId)) {
                            document.getReference().delete();
                            System.out.println("DEBUG: Deleted removed item: " + menuId);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("DEBUG: Failed to load existing cart items: " + e.getMessage());
                });
    }

    public void loadCartFromFirestore(String userId, OnCartLoadedListener listener) {
        if (userId == null || userId.isEmpty()) {
            System.out.println("DEBUG: Cannot load cart - userId is null or empty");
            if (listener != null) listener.onCartLoaded(false);
            return;
        }

        System.out.println("DEBUG: Loading cart from Firestore for user: " + userId);

        db.collection("users").document(userId)
                .collection("cartItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    System.out.println("DEBUG: Found " + queryDocumentSnapshots.size() + " cart items in Firestore");

                    List<CartItem> cartItems = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String menuId = document.getString("menuId");
                            String name = document.getString("name");
                            String imageUrl = document.getString("imageUrl");
                            Double price = document.getDouble("price");
                            Long quantity = document.getLong("quantity");
                            String itemId = document.getString("itemId"); // This should be the auto-generated doc ID

                            System.out.println("DEBUG: Loading item - docId: " + document.getId() + ", itemId: " + itemId + ", menuId: " + menuId + ", name: " + name);

                            // Skip items with missing required fields
                            if (menuId != null && !menuId.isEmpty() && name != null && price != null && quantity != null) {
                                CartItem item = new CartItem(
                                        menuId,
                                        name,
                                        imageUrl,
                                        price,
                                        quantity.intValue()
                                );
                                cartItems.add(item);
                            } else {
                                System.out.println("DEBUG: Skipping invalid document - missing required fields");
                                // Delete invalid document from Firestore
                                document.getReference().delete();
                            }
                        } catch (Exception e) {
                            System.out.println("DEBUG: Error parsing document: " + e.getMessage());
                            // Delete corrupted document from Firestore
                            document.getReference().delete();
                        }
                    }

                    System.out.println("DEBUG: Successfully loaded " + cartItems.size() + " items from Firestore");
                    cartItemsLiveData.setValue(cartItems);
                    if (listener != null) listener.onCartLoaded(true);
                })
                .addOnFailureListener(e -> {
                    System.out.println("DEBUG: Failed to load cart from Firestore: " + e.getMessage());
                    if (listener != null) listener.onCartLoaded(false);
                });
    }

    // Method to remove individual item from cart
    public void removeItem(String menuId) {
        if (menuId == null || menuId.isEmpty()) {
            System.out.println("DEBUG: Cannot remove item - menuId is null or empty");
            return;
        }

        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null) return;

        // Remove from local cart
        CartItem itemToRemove = null;
        for (CartItem item : items) {
            if (item.getMenuId().equals(menuId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            items.remove(itemToRemove);
            cartItemsLiveData.setValue(items);

            // Remove from Firestore - find the document by menuId
            if (userId != null && !userId.isEmpty()) {
                db.collection("users").document(userId)
                        .collection("cartItems")
                        .whereEqualTo("menuId", menuId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete();
                                System.out.println("DEBUG: Successfully removed item: " + menuId);
                            }
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("DEBUG: Failed to remove item " + menuId + ": " + e.getMessage());
                        });
            }
        }
    }

    // Method to update item quantity
    public void updateItemQuantity(String menuId, int quantity) {
        if (menuId == null || menuId.isEmpty()) {
            System.out.println("DEBUG: Cannot update quantity - menuId is null or empty");
            return;
        }

        List<CartItem> items = cartItemsLiveData.getValue();
        if (items == null) return;

        for (CartItem item : items) {
            if (item.getMenuId().equals(menuId)) {
                item.setQuantity(quantity);
                cartItemsLiveData.setValue(items);
                saveCartToFirestore(userId);
                break;
            }
        }
    }

    // Method to clean up corrupted data in Firestore
    public void cleanupCorruptedCartItems(String userId) {
        if (userId == null || userId.isEmpty()) return;

        db.collection("users").document(userId)
                .collection("cartItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String menuId = document.getString("menuId");
                        // Delete documents with empty or null menuId
                        if (menuId == null || menuId.isEmpty()) {
                            document.getReference().delete();
                            System.out.println("DEBUG: Cleaned up corrupted cart item");
                        }
                    }
                });
    }

    public interface OnCartLoadedListener {
        void onCartLoaded(boolean success);
    }
}