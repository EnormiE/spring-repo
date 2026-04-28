package org.example.carrent.repositories.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.carrent.JdbcConnectionManager;
import org.example.carrent.db.JsonFileStorage;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleJdbcRepository implements VehicleRepository {

    // parse attributes from json to Map
    private final Gson gson = new Gson();
    private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();


    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }

        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT id, category, brand, model, year, plate, price, attributes FROM vehicle WHERE id = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding vehicle by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = """
                INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?) 
                ON CONFLICT(id) DO UPDATE SET 
                    category = excluded.category, 
                    brand = excluded.brand, 
                    model = excluded.model, 
                    year = excluded.year, 
                    plate = excluded.plate, 
                    price = excluded.price, 
                    attributes = excluded.attributes
                """;

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getId());
            stmt.setString(2, vehicle.getCategory());
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setString(6, vehicle.getPlate());
            stmt.setDouble(7, vehicle.getPrice());

            // map to json
            String attrJson = gson.toJson(vehicle.getAttributes());
            stmt.setString(8, attrJson);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving vehicle", e);
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        String attrJson = rs.getString("attributes");
        Map<String, Object> attributes = gson.fromJson(attrJson, mapType);

        return Vehicle.builder()
                .id(rs.getString("id"))
                .category(rs.getString("category"))
                .brand(rs.getString("brand"))
                .model(rs.getString("model"))
                .year(rs.getInt("year"))
                .plate(rs.getString("plate"))
                .price(rs.getDouble("price"))
                .attributes(attributes != null ? attributes : new HashMap<>())
                .build();
    }
}