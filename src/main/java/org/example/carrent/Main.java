package org.example.carrent;

import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.example.carrent.repositories.impl.RentalJsonRepository;
import org.example.carrent.repositories.impl.UserJsonRepository;
import org.example.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import org.example.carrent.repositories.impl.VehicleJsonRepository;
import org.example.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        VehicleRepository vehicleRepo = new VehicleJsonRepository();
        UserRepository userRepo = new UserJsonRepository();
        RentalRepository rentalRepo = new RentalJsonRepository();

        AuthService authService = new AuthService(userRepo);
        RentalService rentalService = new RentalService(vehicleRepo, rentalRepo);

        VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
        VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
        VehicleValidator validator = new VehicleValidator(configService);

        VehicleService vehicleService = new VehicleService(validator,vehicleRepo, rentalService);
        UserService userService = new UserService(userRepo, rentalService);

        UserInterface ui = new UserInterface(authService, vehicleService, rentalService, userService, configService);
        ui.start();
    }
}