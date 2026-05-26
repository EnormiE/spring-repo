package org.example.carrent.repositories.impl;

import org.example.carrent.models.Rental;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
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
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }

        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding rental by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        String sql = """
                INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) 
                VALUES (?, ?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET 
                    vehicle_id = excluded.vehicle_id, 
                    user_id = excluded.user_id, 
                    rent_date = excluded.rent_date, 
                    return_date = excluded.return_date
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDateTime());
            stmt.setString(5, rental.getReturnDateTime());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE vehicle_id = ? AND (return_date IS NULL OR return_date = '')";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding active rental for vehicle", e);
        }
        return Optional.empty();
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicle(Vehicle.builder().id(rs.getString("vehicle_id")).build())
                .user(User.builder().id(rs.getString("user_id")).build())
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}