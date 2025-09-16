package com.nibm.pizzamaniamobileapp.model;

import com.google.firebase.firestore.GeoPoint;

public class Address {
    private String addressId;
    private String street;
    private String city;
    private String postalCode;
    private boolean isDefault;
    private GeoPoint geoPoint;

    public Address() {
    }

    public Address(String addressId, String street, String city, String postalCode,
                   boolean isDefault, GeoPoint geoPoint) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
        this.geoPoint = geoPoint;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) sb.append(street);
        if (city != null && !city.isEmpty()) sb.append(", ").append(city);
        if (postalCode != null && !postalCode.isEmpty()) sb.append(" ").append(postalCode);
        return sb.toString();
    }
}
