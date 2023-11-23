package com.andrewcrook.destore.dataaccesslayer;

import javafx.beans.property.*;
public class Product {
    private final IntegerProperty productId = new SimpleIntegerProperty(this, "productId");
    private final StringProperty productName = new SimpleStringProperty(this, "productName");
    private final StringProperty description = new SimpleStringProperty(this, "description");
    private final DoubleProperty price = new SimpleDoubleProperty(this, "price");
    private final IntegerProperty stockLevel = new SimpleIntegerProperty(this, "stockLevel");

    // Constructor
    public Product(int productId, String productName, String description, double price, int stockLevel) {
        setProductId(productId);
        setProductName(productName);
        setDescription(description);
        setPrice(price);
        setStockLevel(stockLevel);
    }

    // Getters and setters for each property

    public int getProductId() {
        return productId.get();
    }

    public void setProductId(int productId) {
        this.productId.set(productId);
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public int getStockLevel() {
        return stockLevel.get();
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel.set(stockLevel);
    }

    public IntegerProperty stockLevelProperty() {
        return stockLevel;
    }
}
