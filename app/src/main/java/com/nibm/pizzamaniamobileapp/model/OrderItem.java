package com.nibm.pizzamaniamobileapp.model;

public class OrderItem {
    private String menuId;
    private String name;
    private int quantity;
    private double price;
    private String imageUrl;

    public OrderItem() { }

    public OrderItem(String menuId, String name, String imageUrl, double price, int quantity) {
        this.menuId = menuId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }
    // getters & setters
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
