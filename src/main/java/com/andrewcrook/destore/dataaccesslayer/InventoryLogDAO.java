package com.andrewcrook.destore.dataaccesslayer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;

    public InventoryLogDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
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

    public List<InventoryLog> getAllInventoryLogs() throws SQLException {
        List<InventoryLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM inventorylog";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int logId = resultSet.getInt("LogID");
                String productName = resultSet.getString("ProductName");
                LocalDateTime date = resultSet.getTimestamp("Date").toLocalDateTime();
                int stockChange = resultSet.getInt("StockChange");
                int newStockLevel = resultSet.getInt("NewStockLevel");

                logs.add(new InventoryLog(logId, productName, date, stockChange, newStockLevel));
            }
        }
        return logs;
    }

    public void insertInventoryLog(InventoryLog log) throws SQLException {
        String sql = "INSERT INTO inventorylog (ProductName, Date, StockChange, NewStockLevel) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, log.getProductName());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(log.getDate()));
            preparedStatement.setInt(3, log.getStockChange());
            preparedStatement.setInt(4, log.getNewStockLevel());

            preparedStatement.executeUpdate();
        }
    }
}
