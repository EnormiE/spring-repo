package org.example.carrent;

import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.example.carrent.repositories.impl.RentalJsonRepository;
import org.example.carrent.repositories.impl.UserJsonRepository;
import org.example.carrent.repositories.impl.VehicleJsonRepository;
import org.example.carrent.services.AuthService;
import org.example.carrent.services.RentalService;

public class Main {
    public static void main(String[] args) {
        VehicleRepository vehicleRepo = new VehicleJsonRepository();
        UserRepository userRepo = new UserJsonRepository();
        RentalRepository rentalRepo = new RentalJsonRepository();

        AuthService authService = new AuthService(userRepo);
        RentalService rentalService = new RentalService(vehicleRepo, rentalRepo);

        UserInterface ui = new UserInterface(vehicleRepo, userRepo, rentalRepo, authService, rentalService);
        ui.start();
    }
}