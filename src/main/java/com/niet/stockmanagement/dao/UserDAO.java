package com.niet.stockmanagement.dao;

import com.niet.stockmanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(rs.getString("role"));
            return user;
        }
    };

    // 🔍 Find user by username
    public User findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username = ?";
            List<User> users = jdbcTemplate.query(sql, userRowMapper, username);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            System.out.println("⚠️ Error in findByUsername: " + e.getMessage());
            return null;
        }
    }

    // 🔐 Validate login credentials
    public User validateUser(String username, String rawPassword) {
        User user = findByUsername(username);

        if (user == null) {
            System.out.println("❌ No user found with username: " + username);
            return null;
        }

        System.out.println("🔐 Hashed password from DB: " + user.getPassword());
        System.out.println("🔑 Raw password entered: " + rawPassword);

        boolean match = passwordEncoder.matches(rawPassword, user.getPassword());
        System.out.println("✅ Password match result: " + match);

        return match ? user : null;
    }

    // ➕ Register new user
    public void save(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), hashedPassword, user.getRole());
        System.out.println("✅ User registered: " + user.getUsername());
    }

    // ❓ Check if username exists
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    // ✅ Change password method
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            String sql = "SELECT password FROM users WHERE username = ?";
            String currentHashedPassword = jdbcTemplate.queryForObject(sql, String.class, username);

            if (passwordEncoder.matches(oldPassword, currentHashedPassword)) {
                String newHashedPassword = passwordEncoder.encode(newPassword);
                String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                int rows = jdbcTemplate.update(updateSql, newHashedPassword, username);
                return rows > 0;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error in changePassword: " + e.getMessage());
        }
        return false;
    }
}
