package com.andrewcrook.destore.dataaccesslayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ProductDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;

    private InventoryLogDAO inventoryLogDAO;

    public ProductDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
        this.inventoryLogDAO = new InventoryLogDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    protected Connection getConnection() throws SQLException {
        Connection jdbcConnection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            jdbcConnection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
        return jdbcConnection;
    }

    public List<Product> listAllProducts() throws SQLException {
        List<Product> listProduct = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int productId = resultSet.getInt("ProductID");
                String productName = resultSet.getString("ProductName");
                String description = resultSet.getString("Description");
                double price = resultSet.getDouble("Price");
                int stockLevel = resultSet.getInt("StockLevel");

                Product product = new Product(productId, productName, description, price, stockLevel);
                listProduct.add(product);
            }
        }
        return listProduct;
    }

        public void insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (ProductName, Description, Price, StockLevel) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getStockLevel());

            preparedStatement.executeUpdate();
        }

        int stockChange = product.getStockLevel();
        InventoryLog log = new InventoryLog(0, product.getProductName(), LocalDateTime.now(), stockChange, product.getStockLevel());
        inventoryLogDAO.insertInventoryLog(log);
    }

    public Product getProduct(int id) throws SQLException {
        Product product = null;
        String sql = "SELECT * FROM products WHERE ProductID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String productName = resultSet.getString("ProductName");
                String description = resultSet.getString("Description");
                double price = resultSet.getDouble("Price");
                int stockLevel = resultSet.getInt("StockLevel");

                product = new Product(id, productName, description, price, stockLevel);
            }
        }
        return product;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET ProductName = ?, Description = ?, Price = ?, StockLevel = ? WHERE ProductID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setInt(4, product.getStockLevel());
            preparedStatement.setInt(5, product.getProductId());

            int affectedRows = preparedStatement.executeUpdate();
            Product oldProduct = getProduct(product.getProductId());
            int stockChange = product.getStockLevel() - oldProduct.getStockLevel();
            if (stockChange != 0) {
                InventoryLog log = new InventoryLog(0, product.getProductName(), LocalDateTime.now(), stockChange, product.getStockLevel());
                inventoryLogDAO.insertInventoryLog(log);
            }
            return affectedRows > 0;
        }

    }


    public boolean deleteProduct(int id) throws SQLException {
        Product product = getProduct(id);
        String sql = "DELETE FROM products WHERE ProductID = ?";

        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            rowDeleted = preparedStatement.executeUpdate() > 0;

            if (product != null) {
                InventoryLog log = new InventoryLog(0, product.getProductName(), LocalDateTime.now(), -product.getStockLevel(), 0);
                inventoryLogDAO.insertInventoryLog(log);
            }
        }
        return rowDeleted;
    }

    public void updateStockLevel(Product product, int additionalStock) throws SQLException {
        String sql = "UPDATE products SET StockLevel = StockLevel + ? WHERE ProductID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, additionalStock);
            preparedStatement.setInt(2, product.getProductId());

            preparedStatement.executeUpdate();
        }
    }

}


