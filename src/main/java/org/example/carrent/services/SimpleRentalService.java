package org.example.carrent.services;

import org.example.carrent.models.Rental;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SimpleRentalService implements RentalServiceInterface {

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public SimpleRentalService(VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Rental rentVehicle(String userId, String vehicleId) throws IllegalStateException, IllegalArgumentException {
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) {
            throw new IllegalArgumentException("Brak pojazdu o takim ID");
        }

        if (rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
            throw new IllegalStateException("Wybrany pojazd jest już przez kogoś wypożyczony");
        }

        if (findActiveRentalByUserId(userId).isPresent()) {
            throw new IllegalStateException("Już masz wypożyczony pojazd, najpierw go zwróć, zanim wypożyczysz nowy");
        }

        Rental rental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicleOpt.get())
                .user(User.builder().id(userId).build())
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        rentalRepository.save(rental);
        return rental;
    }

    public Rental returnVehicle(String userId) throws IllegalStateException {
        Optional<Rental> activeRental = findActiveRentalByUserId(userId);

        if (activeRental.isEmpty()) {
            throw new IllegalStateException("Brak wypożyczonego pojazdu");
        }
        Rental rental = activeRental.get();
        rental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(rental);
        return rental;
    }

    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String id) {
        Optional<Rental> activeRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(id);
        return activeRental.isPresent();
    }

    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String id) {
        return rentalRepository.findAll().stream().filter(rental -> Objects.equals(rental.getUserId(), id)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String id) {
        return findUserRentals(id).stream().filter(Rental::isActive).findFirst();
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}