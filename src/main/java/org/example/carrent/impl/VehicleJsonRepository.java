package org.example.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.carrent.db.JsonFileStorage;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleJsonRepository implements VehicleRepository {

    private final JsonFileStorage<Vehicle> storage;
    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        Type type = new TypeToken<List<Vehicle>>(){}.getType();
        this.storage = new JsonFileStorage<>("vehicles.json", type);
        this.vehicles = storage.load();
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            copy.add(vehicle.copy());
        }
        return copy;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getId().equals(id))
                .findFirst()
                .map(Vehicle::copy);
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        Vehicle copy = vehicle.copy();
        vehicles.removeIf(v -> v.getId().equals(copy.getId()));
        vehicles.add(copy);
        storage.save(vehicles);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(vehicle -> vehicle.getId().equals(id));
        storage.save(vehicles);
    }
}