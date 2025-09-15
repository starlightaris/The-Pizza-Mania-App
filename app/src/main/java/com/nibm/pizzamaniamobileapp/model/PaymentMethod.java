package com.nibm.pizzamaniamobileapp.model;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    private String paymentId;
    private String cardHolderName;
    private String cardNumber; //store only last 4 digits for display in frontend
    private String expiryDate;
    private boolean isDefault;

    public PaymentMethod() { }
    public PaymentMethod(String paymentId, String cardHolderName, String cardNumber, String expiryDate, boolean isDefault) {
        this.paymentId = paymentId;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.isDefault = isDefault;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getMaskedCard() {
        if (cardNumber != null && cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }
}
