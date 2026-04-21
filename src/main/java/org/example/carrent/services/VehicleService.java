package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehicleService {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final RentalService rentalService;


    public VehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, RentalService rentalService) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
    }
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    public Boolean isVehicleRented(String id) {
        return rentalService.vehicleHasActiveRental(id);
    }

    public List<Vehicle> findAvailableVehicles() {
        return findAllVehicles().stream().filter(vehicle -> !isVehicleRented(vehicle.getId())).toList();
    }

    public void removeVehicle(String id) throws Exception {
        if (isVehicleRented(id)) {
            throw new Exception("Nie można usunąć pojazdu, który jest wypożyczony");
        }
        vehicleRepository.deleteById(id);
    }
}
