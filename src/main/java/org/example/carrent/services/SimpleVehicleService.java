package org.example.carrent.services;

import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SimpleVehicleService implements VehicleServiceInterface {

    private final VehicleValidator vehicleValidator;
    private final VehicleRepository vehicleRepository;
    private final SimpleRentalService rentalService;


    public SimpleVehicleService(VehicleValidator vehicleValidator, VehicleRepository vehicleRepository, SimpleRentalService rentalService) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicle = vehicleRepository.save(vehicle);
        return vehicle;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean isVehicleRented(String id) {
        return rentalService.vehicleHasActiveRental(id);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAvailableVehicles() {
        return findAllVehicles().stream().filter(vehicle -> !isVehicleRented(vehicle.getId())).toList();
    }

    public void removeVehicle(String id) throws IllegalStateException {
        if (isVehicleRented(id)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, który jest wypożyczony");
        }
        vehicleRepository.deleteById(id);
    }
}