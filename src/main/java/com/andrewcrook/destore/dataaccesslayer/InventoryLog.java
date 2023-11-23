package com.andrewcrook.destore.dataaccesslayer;

import java.time.LocalDateTime;
import javafx.beans.property.*;

public class InventoryLog {

    private final IntegerProperty logId = new SimpleIntegerProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>();
    private final IntegerProperty stockChange = new SimpleIntegerProperty();
    private final IntegerProperty newStockLevel = new SimpleIntegerProperty();

    // Constructor
    public InventoryLog(int logId, String productName, LocalDateTime date, int stockChange, int newStockLevel) {
        this.logId.set(logId);
        this.productName.set(productName);
        this.date.set(date);
        this.stockChange.set(stockChange);
        this.newStockLevel.set(newStockLevel);
    }

    // Getters and setters
    public int getLogId() {
        return logId.get();
    }
    public IntegerProperty logIdProperty() {
        return logId;
    }
    public void setLogId(int logId) {
        this.logId.set(logId);
    }

    public String getProductName() {
        return productName.get();
    }
    public StringProperty productNameProperty() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public LocalDateTime getDate() {
        return date.get();
    }
    public ObjectProperty<LocalDateTime> dateProperty() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date.set(date);
    }

    public int getStockChange() {
        return stockChange.get();
    }
    public IntegerProperty stockChangeProperty() {
        return stockChange;
    }
    public void setStockChange(int stockChange) {
        this.stockChange.set(stockChange);
    }

    public int getNewStockLevel() {
        return newStockLevel.get();
    }
    public IntegerProperty newStockLevelProperty() {
        return newStockLevel;
    }
    public void setNewStockLevel(int newStockLevel) {
        this.newStockLevel.set(newStockLevel);
    }
}
