package com.niet.stockmanagement.dao;

import com.niet.stockmanagement.model.Product;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/stock_db";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void insertProduct(Product product) {
        String sql = "INSERT INTO products (name, quantity, price, category, image) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getQuantity());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setString(5, product.getImage());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error inserting product: " + e.getMessage());
        }
    }

    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, quantity = ?, price = ?, category = ?, image = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getQuantity());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setString(5, product.getImage());
            stmt.setInt(6, product.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching all products: " + e.getMessage());
        }

        return products;
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching product by ID: " + e.getMessage());
        }

        return null;
    }

    public void sellOneItem(int id) {
        String sql = "UPDATE products SET quantity = quantity - 1 WHERE id = ? AND quantity > 0";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error selling product: " + e.getMessage());
        }
    }

    public void deleteProductById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
        }
    }

    public List<Product> searchByKeyword(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error searching products: " + e.getMessage());
        }

        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error filtering by category: " + e.getMessage());
        }

        return products;
    }

    // ✅ For Analytics Dashboard

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching categories: " + e.getMessage());
        }

        return categories;
    }

    public List<Integer> getStockCountPerCategory() {
        List<Integer> counts = new ArrayList<>();
        String sql = "SELECT SUM(quantity) AS total FROM products GROUP BY category";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                counts.add(rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching stock counts: " + e.getMessage());
        }

        return counts;
    }

    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching product count: " + e.getMessage());
        }
        return 0;
    }

    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE quantity < 10";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching low stock count: " + e.getMessage());
        }
        return 0;
    }

    public List<Product> getTopExpensiveProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY price DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching top expensive products: " + e.getMessage());
        }

        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setQuantity(rs.getInt("quantity"));
        product.setPrice(rs.getDouble("price"));
        product.setCategory(rs.getString("category"));
        product.setImage(rs.getString("image"));
        return product;
    }
}
