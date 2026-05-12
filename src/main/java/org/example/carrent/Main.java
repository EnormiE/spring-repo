package org.example.carrent;

import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleCategoryConfigRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.example.carrent.repositories.impl.*;
import org.example.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("ustaw env: json, jdbc lub hibernate");
            return;
        }

        if (args[0].equals("json")) {
            System.out.println("repo: JSON");

            VehicleRepository vehicleRepo = new VehicleJsonRepository();
            UserRepository userRepo = new UserJsonRepository();
            RentalRepository rentalRepo = new RentalJsonRepository();

            SimpleAuthService authService = new SimpleAuthService(userRepo);
            SimpleRentalService rentalService = new SimpleRentalService(vehicleRepo, rentalRepo);

            VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
            VehicleValidator validator = new VehicleValidator(configService);

            SimpleVehicleService vehicleService = new SimpleVehicleService(validator, vehicleRepo, rentalService);
            SimpleUserService userService = new SimpleUserService(userRepo, rentalService);

            UserInterface ui = new UserInterface(authService, vehicleService, rentalService, userService, configService);
            ui.start();
        }
        else if (args[0].equals("jdbc")) {
            System.out.println("repo: JDBC");
            VehicleRepository vehicleRepo = new VehicleJdbcRepository();
            UserRepository userRepo = new UserJdbcRepository();
            RentalRepository rentalRepo = new RentalJdbcRepository();

            SimpleAuthService authService = new SimpleAuthService(userRepo);
            SimpleRentalService rentalService = new SimpleRentalService(vehicleRepo, rentalRepo);

            VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
            VehicleValidator validator = new VehicleValidator(configService);

            SimpleVehicleService vehicleService = new SimpleVehicleService(validator, vehicleRepo, rentalService);
            SimpleUserService userService = new SimpleUserService(userRepo, rentalService);

            UserInterface ui = new UserInterface(authService, vehicleService, rentalService, userService, configService);
            ui.start();
        }
        else if (args[0].equals("hibernate")) {
            System.out.println("repo: HIBERNATE");

            RentalHibernateRepository rentalRepo = new RentalHibernateRepository();
            VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository();
            UserHibernateRepository userRepo = new UserHibernateRepository();

            AuthServiceInterface authService = new AuthHibernateService(userRepo);
            RentalServiceInterface rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);

            VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
            VehicleCategoryConfigService configService = new VehicleCategoryConfigService(configRepo);
            VehicleValidator validator = new VehicleValidator(configService);

            VehicleServiceInterface vehicleService = new VehicleHibernateService(validator, vehicleRepo, rentalService);
            UserServiceInterface userService = new UserHibernateService(userRepo, rentalService);

            UserInterface ui = new UserInterface(authService, vehicleService, rentalService, userService, configService);
            ui.start();
        }
    }
}