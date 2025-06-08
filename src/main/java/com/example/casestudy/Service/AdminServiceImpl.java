package com.example.casestudy.Service;

import com.example.casestudy.model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class AdminServiceImpl implements AdminService {

    @Override
    public Admin findByUsername(String username) {
        Admin admin = null;
        String query = "SELECT * FROM admins WHERE username = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setFullName(rs.getString("full_name"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }

    @Override
    public boolean checkPassword(Admin admin, String rawPassword) {
        if (admin == null || admin.getPasswordHash() == null) {
            return false;
        }

        return admin.getPasswordHash().equals(rawPassword);
    }
}
