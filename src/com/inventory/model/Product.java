package com.inventory.model;
import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private int reorderLevel;
    public Product() {}
    public Product(int id, String name, String category, int quantity, double price, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getReorderLevel() {
        return reorderLevel;
    }
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    public boolean isLowStock() {
        return quantity < reorderLevel;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", reorderLevel=" + reorderLevel +
                '}';
    }
}
