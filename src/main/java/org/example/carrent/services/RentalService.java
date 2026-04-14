package org.example.carrent.services;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class RentalService {

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public RentalService(VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public boolean rentVehicle(String vehicleId, String userId) {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            return false;
        }

        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            return false;
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicleId(vehicleId)
                .userId(userId)
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        rentalRepository.save(rental);
        return true;
    }

    public boolean returnVehicle(String vehicleId) {
        Optional<Rental> activeRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);

        if (activeRental.isEmpty()) {
            return false;
        }

        Rental rental = activeRental.get();
        rental.setReturnDateTime(LocalDateTime.now().toString());

        rentalRepository.save(rental);
        return true;
    }
}