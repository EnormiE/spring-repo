package org.example.carrent;

import java.util.Scanner;

public class UserInterface {

    public static void launchMenu(IVehicleRepository vehRepo, IUserRepository userRepo) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();
        Authentication authentication = new Authentication(userRepo);
        User user = authentication.authenticate(login, password);
        if (user.getRole().equals(Role.USER)) {
            while (true) {
                System.out.println("\nWitaj " + user.getLogin() + "!");
                System.out.println("1. Wypożycz");
                System.out.println("2. Zwróć");
                System.out.println("3. Wyświetl swoje dane");
                System.out.println("0. Wyjście");
                String select = scanner.nextLine();
                switch (select) {
                    case "1":
                        if (user.getRentedVehicleId() != null) {
                            System.out.println("Niepowodzenie (Masz już wypożyczony pojazd)");
                        } else {
                            System.out.println("Podaj id pojazdu do wypożyczenia:");
                            String id = scanner.nextLine();
                            if (vehRepo.rentVehicle(id)) {
                                System.out.println("Sukces");
                                user.setRentedVehicleId(id);
                                userRepo.update(user);
                            } else {
                                System.out.println("Niepowodzenie (Nie możesz wypożyczyć pojazdu, który juz jest wypożyczony)");
                            }
                        }
                        break;
                    case "2":
                        String id = user.getRentedVehicleId();
                        if (vehRepo.returnVehicle(id)) {
                            System.out.println("Sukces");
                            user.setRentedVehicleId(null);
                            userRepo.update(user);
                        } else {
                            System.out.println("Niepowodzenie (Nie masz żadnego wypożyczonego pojazdu)");
                        }
                        break;
                    case "3":
                        System.out.println(
                                "---USER_DATA---" +
                                "\nLogin: " + user.getLogin() +
                                "\nPassword: " + user.getPassword() +
                                "\nRole: " + user.getRole() +
                                "\nRentedVehicleId: " + user.getRentedVehicleId()
                        );
                        if (user.getRentedVehicleId() != null) {
                            Vehicle veh = vehRepo.getVehicle(user.getRentedVehicleId());
                            System.out.println(
                                    "---VEHICLE_DATA---" +
                                    "\nID: " + veh.getId() +
                                    "\nType: " + veh.getClass().getSimpleName() +
                                    "\nBrand: " + veh.getBrand() +
                                    "\nModel: " + veh.getModel() +
                                    "\nYear: " + veh.getYear() +
                                    "\nPrice: " + veh.getPrice()
                            );
                        }
                        break;
                    case "0":
                        return;
                }
            }
        } else if (user.getRole().equals(Role.ADMIN)) {
            while (true) {
                System.out.println("\nWitaj " + user.getLogin() + "!");
                System.out.println("1. Dodaj pojazd");
                System.out.println("2. Usuń pojazd");
                System.out.println("3. Przeglądaj pojazdy");
                System.out.println("4. Przeglądaj użytkowników");
                System.out.println("5. Dodaj użytkownika");
                System.out.println("6. Usuń użytkownika");
                System.out.println("0. Wyjście");
                String select = scanner.nextLine();
                switch (select) {
                    case "1": {
                        System.out.print("ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Type: ");
                        String type = scanner.nextLine();
                        System.out.print("Brand: ");
                        String brand = scanner.nextLine();
                        System.out.print("Model: ");
                        String model = scanner.nextLine();
                        System.out.print("Year: ");
                        String year = scanner.nextLine();
                        System.out.print("Price: ");
                        String price = scanner.nextLine();
                        type = type.toUpperCase();
                        if (type.equals("MOTORCYCLE")) {
                            System.out.print("Category: ");
                            String category = scanner.nextLine();
                            Motorcycle m = new Motorcycle(id, brand, model, Integer.parseInt(year), Float.parseFloat(price), false, MotorcycleCategory.valueOf(category));
                            if (vehRepo.add(m)) {
                                System.out.println("Sukces");
                            } else {
                                System.out.println("Niepowodzenie (To ID jest zajęte)");
                            }
                        } else if (type.equals("CAR")) {
                            Car c = new Car(id, brand, model, Integer.parseInt(year), Float.parseFloat(price), false);
                            if (vehRepo.add(c)) {
                                System.out.println("Sukces");
                            } else {
                                System.out.println("Niepowodzenie (To ID jest zajęte)");
                            }
                        }
                        break;
                    }
                    case "2": {
                        System.out.print("ID pojazdu do usunięcia: ");
                        String id = scanner.nextLine();
                        if (vehRepo.remove(id)) {
                            System.out.println("Sukces");
                        } else {
                            System.out.println("Niepowodzenie (Błędne ID lub pojazd jest wypożyczony)");
                        }
                        break;
                    }
                    case "3":
                        for (Vehicle v : vehRepo.getVehicles()) {
                            System.out.println(v.toString());
                        }
                        break;
                    case "4":
                        for (User u : userRepo.getUsers()) {
                            System.out.println(u.toString());
                            if (u.getRentedVehicleId() != null) {
                                System.out.print("   rentedVehData:\n      ");
                                System.out.println(vehRepo.getVehicle(u.getRentedVehicleId()).toString());
                            }
                        }
                        break;
                    case "5":
                        System.out.print("Login: ");
                        String l = scanner.nextLine();
                        System.out.print("Password: ");
                        String p = scanner.nextLine();
                        System.out.print("Role: ");
                        String r = scanner.nextLine();
                        if (authentication.register(l, p, r) != null) {
                            System.out.println("Sukces");
                        }
                        else {
                            System.out.println("Niepowodzenie (Login jest zajęty)");
                        }
                        break;
                    case "6":
                        System.out.print("Login: ");
                        String l1 = scanner.nextLine();
                        if (userRepo.remove(userRepo.getUser(l1))) {
                            System.out.println("Sukces");
                        }
                        else {
                            System.out.println("Niepowodzenie (Użytkownik ma wypożyczony pojazd)");
                        }
                        break;
                    case "0":
                        return;
                }
            }
        }
    }
}