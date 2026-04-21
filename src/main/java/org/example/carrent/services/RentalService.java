package org.example.carrent.services;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalService {

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public RentalService(VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public void rentVehicle(String userId, String vehicleId) throws Exception {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            throw new Exception("Brak pojazdu o takim ID");
        }

        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new Exception("Wybrany pojazd jest już przez kogoś wypożyczony");
        }

        if (findActiveRentalByUserId(userId).isPresent()) {
            throw new Exception("Już masz wypożyczony pojazd, najpierw go zwróć, zanim wypożyczysz nowy");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicleId(vehicleId)
                .userId(userId)
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        rentalRepository.save(rental);
    }

    public void returnVehicle(String userId) throws Exception {
        Optional<Rental> activeRental = findActiveRentalByUserId(userId);

        if (activeRental.isEmpty()) {
            throw new Exception("Brak wypożyczonego pojazdu");
        }
        Rental rental = activeRental.get();
        rental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(rental);
    }

    public boolean vehicleHasActiveRental(String id) {
        Optional<Rental> activeRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(id);
        return activeRental.isPresent();
    }

    public List<Rental> findUserRentals(String id) {
        return rentalRepository.findAll().stream().filter(rental -> Objects.equals(rental.getUserId(), id)).toList();
    }

    public Optional<Rental> findActiveRentalByUserId(String id) {
        return findUserRentals(id).stream().filter(Rental::isActive).findFirst();
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}