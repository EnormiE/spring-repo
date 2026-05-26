package org.example.carrent.web;

import org.example.carrent.models.Rental;
import org.example.carrent.services.RentalServiceInterface;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;

    public RentalController(RentalServiceInterface rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rent(@PathVariable String userId, @PathVariable String vehicleId) throws Exception {
        return rentalService.rentVehicle(userId, vehicleId);
    }

    @PostMapping("/users/{userId}/return")
    public Rental returnVehicle(@PathVariable String userId) throws Exception {
        return rentalService.returnVehicle(userId);
    }
}