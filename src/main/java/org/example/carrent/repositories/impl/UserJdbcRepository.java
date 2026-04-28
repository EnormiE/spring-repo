package org.example.carrent.repositories.impl;

import org.example.carrent.JdbcConnectionManager;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJdbcRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, login, password_hash, role FROM users";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE id = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password_hash, role FROM users WHERE login = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding user by login", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (id, login, password_hash, role) 
                VALUES (?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET 
                    login = excluded.login, 
                    password_hash = excluded.password_hash, 
                    role = excluded.role
                """;

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getId());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getRole() != null ? user.getRole().name() : null);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving user", e);
        }
        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting user", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;

        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(role)
                .build();
    }
}