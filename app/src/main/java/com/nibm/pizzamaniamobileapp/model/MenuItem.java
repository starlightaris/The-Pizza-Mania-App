package com.nibm.pizzamaniamobileapp.model;

public class MenuItem {
    private String menuId;
    private String name;
    private String description;
    private double price;
    private boolean available;
    private String category;
    private String imageUrl;

    public MenuItem() { }

    public MenuItem(String menuId, String name, String description, double price,
                    boolean available, String category, String imageUrl) {
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
