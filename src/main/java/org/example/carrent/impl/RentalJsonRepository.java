package org.example.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.carrent.db.JsonFileStorage;
import org.example.carrent.models.Rental;
import org.example.carrent.repositories.RentalRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository {

    private final JsonFileStorage<Rental> storage;
    private final List<Rental> rentals;

    public RentalJsonRepository() {
        Type type = new TypeToken<List<Rental>>(){}.getType();
        this.storage = new JsonFileStorage<>("rentals.json", type);
        this.rentals = storage.load();
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> copy = new ArrayList<>();
        for (Rental rental : rentals) {
            copy.add(rental.copy());
        }
        return copy;
    }

    @Override
    public Optional<Rental> findById(String id) {
        return rentals.stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst()
                .map(Rental::copy);
    }

    @Override
    public Rental save(Rental rental) {
        Rental copy = rental.copy();
        rentals.removeIf(r -> r.getId().equals(copy.getId()));
        rentals.add(copy);
        storage.save(rentals);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId) && rental.isActive())
                .findFirst()
                .map(Rental::copy);
    }
}