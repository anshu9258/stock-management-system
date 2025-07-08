package com.niet.stockmanagement.dao;

import com.niet.stockmanagement.model.Category;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/stock_db";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "root";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public void addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error adding category: " + e.getMessage());
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching categories: " + e.getMessage());
        }
        return categories;
    }
}
