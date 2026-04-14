package org.example.carrent;

import org.example.carrent.repositories.impl.VehicleJsonRepository;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRepositoryTest {

    private VehicleRepository repo;

    @BeforeEach
    void setUp() {
        repo = new VehicleJsonRepository();
        if (repo.findAll().isEmpty()) {
            repo.save(Vehicle.builder().id("999").brand("Test").price(100.0).build());
        }
    }

    @Test
    void getVehiclesShouldReturnDeepCopy() {
        List<Vehicle> vehicles1 = repo.findAll();
        List<Vehicle> vehicles2 = repo.findAll();

        assertNotSame(vehicles1, vehicles2);
        assertNotSame(vehicles1.get(0), vehicles2.get(0));
    }

    @Test
    void addingToReturnedListShouldNotChangeRepository() {
        List<Vehicle> vehicles = repo.findAll();
        int repoSizeBefore = repo.findAll().size();

        vehicles.add(Vehicle.builder().id("100").brand("Test").model("Test").year(2026).price(1).build());

        int repoSizeAfter = repo.findAll().size();
        assertEquals(repoSizeBefore, repoSizeAfter);
    }

    @Test
    void changingReturnedVehicleShouldNotChangeRepository() {
        List<Vehicle> vehicles = repo.findAll();
        Vehicle copy = vehicles.get(0);

        double originalPrice = repo.findAll().get(0).getPrice();
        copy.setPrice(originalPrice + 500);

        double repoPriceAfterChange = repo.findAll().get(0).getPrice();
        assertEquals(originalPrice, repoPriceAfterChange);
    }
}