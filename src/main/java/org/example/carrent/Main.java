package org.example.carrent;

public class Main {
    static void main() {
        IVehicleRepository vehRepo = new VehicleRepositoryImpl();
        IUserRepository userRepo = new UserRepository();
//        System.out.println("Hello world!");
        UserInterface.launchMenu(vehRepo, userRepo);
    }
}
