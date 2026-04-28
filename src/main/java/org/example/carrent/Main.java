package org.example.carrent;

import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.example.carrent.repositories.impl.*;
import org.example.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        if (args[0].equals("json")) {
            System.out.println("repo: JSON");

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
        else if (args[0].equals("jdbc")) {
            System.out.println("repo: JDBC");
            VehicleRepository vehicleRepo = new VehicleJdbcRepository();
            UserRepository userRepo = new UserJdbcRepository();
            RentalRepository rentalRepo = new RentalJdbcRepository();

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
}