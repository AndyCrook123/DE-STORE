package com.andrewcrook.destore.presentationlayer;

import com.andrewcrook.destore.dataaccesslayer.Product;
import com.andrewcrook.destore.businesslogiclayer.ProductService;
import com.andrewcrook.destore.dataaccesslayer.InventoryLogDAO;
import com.andrewcrook.destore.dataaccesslayer.InventoryLog;
import com.andrewcrook.destore.businesslogiclayer.util.EmailUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.application.Platform;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.SQLException;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class DestoreController {

    public TextField deleteProductIdField;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockLevelField;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockLevelColumn;
    @FXML
    private VBox editProductForm;
    @FXML
    private TextField customerNameField, customerAgeField, loanAmountField, loanDurationField;
    @FXML
    private TextField editNameField, editDescriptionField, editPriceField, editStockLevelField;
    @FXML
    private LineChart<String, Number> salesChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private TextField orderIdField;
    @FXML
    private TextField orderQuantityField;
    @FXML
    private TableView<InventoryLog> inventoryLogTableView;
    @FXML
    private TableColumn<InventoryLog, Integer> logIdColumn;
    @FXML
    private TableColumn<InventoryLog, String> productNameColumnLog;
    @FXML
    private TableColumn<InventoryLog, String> dateColumn;
    @FXML
    private TableColumn<InventoryLog, Integer> stockChangeColumn;
    @FXML
    private TableColumn<InventoryLog, Integer> newStockLevelColumn;
    private InventoryLogDAO inventoryLogDAO;
    @FXML
    private Stage editDialogStage;
    private ProductService productService;
    private Product currentProduct;

    @FXML
    private void initialize() {

        String jdbcURL = "jdbc:mysql://localhost:3306/de-store";
        String jdbcUsername = "root";
        String jdbcPassword = "andrewcrook";
        productService = new ProductService(jdbcURL, jdbcUsername, jdbcPassword);
        inventoryLogDAO = new InventoryLogDAO(jdbcURL, jdbcUsername, jdbcPassword);

        productIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());
        productNameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        stockLevelColumn.setCellValueFactory(cellData -> cellData.getValue().stockLevelProperty().asObject());

        loadProductData();
        scheduleDataRefresh();
        setupAxes();
        setupChartData();
        setupInventoryLogTable();
        loadInventoryLogData();
    }
    private void scheduleDataRefresh() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        refreshProductData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 2000);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndSendLowStockAlerts();
            }
        },0,10000);

    }

    private void refreshProductData() throws SQLException {
        ObservableList<Product> productList = FXCollections.observableArrayList(productService.getAllProducts());
        productTableView.setItems(productList);
    }
    private void loadProductData() {
        try {
            ObservableList<Product> productList = FXCollections.observableArrayList(productService.getAllProducts());
            productTableView.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading product data");
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            String productName = productNameField.getText();
            String description = descriptionField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stockLevel = Integer.parseInt(stockLevelField.getText());

            Product newProduct = new Product(0, productName, description, price, stockLevel);
            productService.addProduct(newProduct);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error adding product");
        } finally {
            productNameField.clear();
            descriptionField.clear();
            priceField.clear();
            stockLevelField.clear();
        }
        refreshInventoryLogData();
    }

    @FXML
    private void handleDeleteProduct() {
        try {
            int productId = Integer.parseInt(deleteProductIdField.getText());
            productService.deleteProduct(productId);
            refreshProductData();
        } catch (NumberFormatException e) {
            System.out.println("Invalid Product ID");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting product");
        }
        finally {
            deleteProductIdField.clear();
        }
        refreshInventoryLogData();
    }
    @FXML
    private void handleEditProduct() {
        try {
            int productId = Integer.parseInt(deleteProductIdField.getText());
            Product productToEdit = productService.getProduct(productId);
            if (productToEdit != null) {
                currentProduct = productToEdit;
                populateEditFields(productToEdit);
                showEditProductDialog(true);
            } else {
                System.out.println("Product not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Product ID");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving product data");
        }
        refreshInventoryLogData();
    }

    private void showEditProductDialog(boolean show) {
            deleteProductIdField.clear();
            editProductForm.setVisible(show);
            editProductForm.setManaged(show);
    }

    private void populateEditFields(Product product) {
        editNameField.setText(product.getProductName());
        editDescriptionField.setText(product.getDescription());
        editPriceField.setText(String.valueOf(product.getPrice()));
        editStockLevelField.setText(String.valueOf(product.getStockLevel()));
    }

    @FXML
    private void handleSaveEdit() {
        try {

            currentProduct.setProductName(editNameField.getText());
            currentProduct.setDescription(editDescriptionField.getText());
            currentProduct.setPrice(Double.parseDouble(editPriceField.getText()));
            currentProduct.setStockLevel(Integer.parseInt(editStockLevelField.getText()));

            productService.updateProduct(currentProduct);

            if (editDialogStage != null) {
                editDialogStage.close();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving product");
        }
        finally {
            showEditProductDialog(false);
        }
        refreshInventoryLogData();
    }

    @FXML
    private void handleCancelEdit() {
        if (editDialogStage != null) {
            editDialogStage.close();
            showEditProductDialog(false);
        } else {
            showEditProductDialog(false);
        }
    }
    @FXML
    private void handleFinanceApproval() {

        String name = customerNameField.getText();
        String age = customerAgeField.getText();
        String amount = loanAmountField.getText();
        String duration = loanDurationField.getText();

        showFinanceAlert("Connecting to Finance Portal",
                "Processing finance approval for:\n" +
                        "Name: " + name + "\nAge: " + age + "\nAmount: " + amount +
                        "\nDuration: " + duration + " years");

        // Clear the fields after submission
        customerNameField.clear();
        customerAgeField.clear();
        loanAmountField.clear();
        loanDurationField.clear();
    }

    private void showFinanceAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void setupAxes() {
        xAxis.setLabel("Months");
        yAxis.setLabel("Sales (£)");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(6000);
        yAxis.setTickUnit(500);

        xAxis.getCategories().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );
    }
    @FXML
    private void setupChartData() {
        XYChart.Series<String, Number> salesData = new XYChart.Series<>();
        salesData.setName("Monthly Sales");

        // Sample hardcoded data
        salesData.getData().add(new XYChart.Data<>("January", 2000));
        salesData.getData().add(new XYChart.Data<>("February", 3500));
        salesData.getData().add(new XYChart.Data<>("March", 3100));
        salesData.getData().add(new XYChart.Data<>("April", 2800));
        salesData.getData().add(new XYChart.Data<>("May", 2500));
        salesData.getData().add(new XYChart.Data<>("June", 3200));
        salesData.getData().add(new XYChart.Data<>("July", 3800));
        salesData.getData().add(new XYChart.Data<>("August", 3400));
        salesData.getData().add(new XYChart.Data<>("September", 2700));
        salesData.getData().add(new XYChart.Data<>("October", 2300));
        salesData.getData().add(new XYChart.Data<>("November", 3000));
        salesData.getData().add(new XYChart.Data<>("December", 3300));

        salesChart.getData().add(salesData);
    }

    public void checkAndSendLowStockAlerts() {
        try {
            List<Product> lowStockProducts = productService.getLowStockProducts(100);
            if (!lowStockProducts.isEmpty()) {
                StringBuilder body = new StringBuilder("Low stock alert for the following products:\n");
                for (Product product : lowStockProducts) {
                    body.append(product.getProductName())
                            .append(" - Stock Level: ")
                            .append(product.getStockLevel())
                            .append("\n");
                }
                EmailUtil.sendEmail("andrewcrook7@gmail.com", "Low Stock Alert", body.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStock() {
        try {
            int productId = Integer.parseInt(orderIdField.getText());
            int amountToAdd = Integer.parseInt(orderQuantityField.getText());

            Product productToUpdate = productService.getProduct(productId);
            if (productToUpdate != null) {
                int newStockLevel = productToUpdate.getStockLevel() + amountToAdd;
                productToUpdate.setStockLevel(newStockLevel);
                productService.updateProduct(productToUpdate);
                refreshProductData();
            } else {
                System.out.println("Product not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating product stock");
        } finally {
            orderIdField.clear();
            orderQuantityField.clear();
            refreshInventoryLogData();
        }
    }

    private void setupInventoryLogTable() {
        logIdColumn.setCellValueFactory(cellData -> cellData.getValue().logIdProperty().asObject());
        productNameColumnLog.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());
        stockChangeColumn.setCellValueFactory(cellData -> cellData.getValue().stockChangeProperty().asObject());
        newStockLevelColumn.setCellValueFactory(cellData -> cellData.getValue().newStockLevelProperty().asObject());
    }

    private void loadInventoryLogData() {
        try {
            ObservableList<InventoryLog> logList = FXCollections.observableArrayList(inventoryLogDAO.getAllInventoryLogs());
            inventoryLogTableView.setItems(logList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading inventory log data");
        }
    }
    private void refreshInventoryLogData() {
        try {
            ObservableList<InventoryLog> logList = FXCollections.observableArrayList(inventoryLogDAO.getAllInventoryLogs());
            inventoryLogTableView.setItems(logList);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading inventory log data");
        }
    }

}
