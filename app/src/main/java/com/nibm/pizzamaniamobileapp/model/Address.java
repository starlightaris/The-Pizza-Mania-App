package com.nibm.pizzamaniamobileapp.model;

public class Address {
    private String addressId;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String postalCode;
    private boolean isDefault;

    public Address() {
    }

    public Address(String addressId, String fullName, String phone, String street, String city, String postalCode, boolean isDefault) {
        this.addressId = addressId;
        this.fullName = fullName;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null && !street.isEmpty()) sb.append(street);
        if (city != null && !city.isEmpty()) sb.append(", ").append(city);
        if (postalCode != null && !postalCode.isEmpty()) sb.append(" ").append(postalCode);
        return sb.toString();
    }
}


