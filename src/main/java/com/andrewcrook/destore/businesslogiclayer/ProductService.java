package com.andrewcrook.destore.businesslogiclayer;

import com.andrewcrook.destore.dataaccesslayer.Product;
import com.andrewcrook.destore.dataaccesslayer.ProductDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {
    private ProductDAO productDAO;

    public ProductService(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.productDAO = new ProductDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.listAllProducts();
    }

    public void addProduct(Product product) throws SQLException {
        productDAO.insertProduct(product);
    }


    public Product getProduct(int productId) throws SQLException {
        return productDAO.getProduct(productId);
    }

    public boolean updateProduct(Product product) throws SQLException {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int productId) throws SQLException {
        return productDAO.deleteProduct(productId);
    }

    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        List<Product> allProducts = productDAO.listAllProducts();
        return allProducts.stream()
                .filter(product -> product.getStockLevel() < threshold)
                .collect(Collectors.toList());
    }
}
