package org.example.carrent.repositories.impl;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.id, r.vehicle_id, r.user_id, r.rent_date, r.return_date, " +
                "v.category, v.brand, v.model, v.year, v.plate, v.price, " +
                "u.login, u.password_hash, u.role " +
                "FROM rental r " +
                "JOIN vehicles v ON r.vehicle_id = v.id " +
                "JOIN users u ON r.user_id = u.id";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT r.id, r.vehicle_id, r.user_id, r.rent_date, r.return_date, " +
                "v.category, v.brand, v.model, v.year, v.plate, v.price, " +
                "u.login, u.password_hash, u.role " +
                "FROM rental r " +
                "JOIN vehicles v ON r.vehicle_id = v.id " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding rental by id", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(java.util.UUID.randomUUID().toString());
        }

        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) 
                VALUES (?, ?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET 
                    vehicle_id = excluded.vehicle_id, 
                    user_id = excluded.user_id, 
                    rent_date = excluded.rent_date, 
                    return_date = excluded.return_date
                """;

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDateTime());
            stmt.setString(5, rental.getReturnDateTime());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT r.id, r.vehicle_id, r.user_id, r.rent_date, r.return_date, " +
                "v.category, v.brand, v.model, v.year, v.plate, v.price, " +
                "u.login, u.password_hash, u.role " +
                "FROM rental r " +
                "JOIN vehicles v ON r.vehicle_id = v.id " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.vehicle_id = ? AND (r.return_date IS NULL OR r.return_date = '')";

        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding active rental for vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicle(Vehicle.builder()
                        .id(rs.getString("vehicle_id"))
                        .category(rs.getString("category"))
                        .brand(rs.getString("brand"))
                        .model(rs.getString("model"))
                        .year(rs.getInt("year"))
                        .plate(rs.getString("plate"))
                        .price(rs.getDouble("price"))
                        .build())
                .user(User.builder()
                        .id(rs.getString("user_id"))
                        .login(rs.getString("login"))
                        .passwordHash(rs.getString("password_hash"))
                        .role(rs.getString("role") != null ? Role.valueOf(rs.getString("role")) : null)
                        .build())
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}