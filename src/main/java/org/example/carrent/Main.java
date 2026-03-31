package org.example.carrent;

import org.example.carrent.impl.VehicleRepository;

public class Main {
    static void main() {
        IVehicleRepository vehRepo = new VehicleRepository();
        IUserRepository userRepo = new UserRepository();
//        System.out.println("Hello world!");
        UserInterface.launchMenu(vehRepo, userRepo);
    }
}
