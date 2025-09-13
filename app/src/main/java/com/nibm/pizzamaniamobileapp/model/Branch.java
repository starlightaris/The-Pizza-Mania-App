package com.nibm.pizzamaniamobileapp.model;

import com.google.firebase.firestore.GeoPoint;

public class Branch {
    private String branchId;
    private String name;
    private GeoPoint location;
    private String contact;

    public Branch() {}

    public Branch(String branchId, String name, GeoPoint location, String contact) {
        this.branchId = branchId;
        this.name = name;
        this.location = location;
        this.contact = contact;
    }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public GeoPoint getLocation() {
        return location;
    }
    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
