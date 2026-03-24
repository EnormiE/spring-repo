package org.example.carrent;

import java.util.Scanner;

public class UserInterface {

    public static void launchMenu(IVehicleRepository vehRepo, IUserRepository userRepo) {
        Scanner scanner = new Scanner(System.in);

//            System.out.println("1. Pokaż wszystkie pojazdy\n2. Wypożycz\n3. Zwróć\n4. Wyjdź");
//            String select = scanner.nextLine();

//            if (select.equals("1")) {
//                for (Vehicle v : vehRepo.getVehicles()) {
//                    System.out.println(v.toString());
//                }
//            }
//            else if (select.equals("2")) {
//                System.out.print("Podaj ID do wypożyczenia: ");
//                String id = scanner.nextLine();
//                if (vehRepo.rentVehicle(id)) {
//                    System.out.println("Sukces");
//                }
//                else {
//                    System.out.println("Niepowodzenie (Nie możesz wypożyczyć pojazdu, który juz jest wypożyczony)");
//                }
//            }
//            else if (select.equals("3")) {
//                System.out.print("Podaj ID do zwrotu: ");
//                String id = scanner.nextLine();
//                if (vehRepo.returnVehicle(id)) {
//                    System.out.println("Sukces");
//                }
//                else {
//                    System.out.println("Niepowodzenie (Nie możesz zwrócić pojazdu, który nie jest wypożyczony)");
//                }
//            }
//            else if (select.equals("4")) {
//                break;
//            }

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
                System.out.println("4. Wyjście");
                String select = scanner.nextLine();
                if (select.equals("1")) {
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
                } else if (select.equals("2")) {
                    String id = user.getRentedVehicleId();
                    if (vehRepo.returnVehicle(id)) {
                        System.out.println("Sukces");
                        user.setRentedVehicleId(null);
                        userRepo.update(user);
                    } else {
                        System.out.println("Niepowodzenie (Nie masz żadnego wypożyczonego pojazdu)");
                    }
                } else if (select.equals("3")) {
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
                } else if (select.equals("4")) {
                    break;
                }
            }
        } else if (user.getRole().equals(Role.ADMIN)) {
            while (true) {
                System.out.println("\nWitaj " + user.getLogin() + "!");
                System.out.println("1. Dodaj pojazd");
                System.out.println("2. Usuń pojazd");
                System.out.println("3. Przeglądaj pojazdy");
                System.out.println("4. Przeglądaj użytkowników");
                System.out.println("5. Wyjście");
                String select = scanner.nextLine();
                if (select.equals("1")) {
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
                        }
                        else {
                            System.out.println("Niepowodzenie (To ID jest zajęte)");
                        }
                    }
                    else if (type.equals("CAR")) {
                        Car c = new Car(id, brand, model, Integer.parseInt(year), Float.parseFloat(price), false);
                        if (vehRepo.add(c)) {
                            System.out.println("Sukces");
                        }
                        else {
                            System.out.println("Niepowodzenie (To ID jest zajęte)");
                        }
                    }
                }
                if (select.equals("2")) {
                    System.out.print("ID pojazdu do usunięcia: ");
                    String id = scanner.nextLine();
                    if (vehRepo.remove(id)) {
                        System.out.println("Sukces");
                    }
                    else {
                        System.out.println("Niepowodzenie (Błędne ID)");
                    }
                }
                if (select.equals("3")) {
                    for (Vehicle v : vehRepo.getVehicles()) {
                        System.out.println(v.toString());
                    }
                }
                if (select.equals("4")) {
                    for (User u : userRepo.getUsers()) {
                        System.out.println(u.toString());
                        if (u.getRentedVehicleId() != null) {
                            System.out.print("   rentedVehData:\n      ");
                            System.out.println(vehRepo.getVehicle(u.getRentedVehicleId()).toString());
                        }
                    }
                }
                if (select.equals("5")) {
                    break;
                }
            }
        }
    }
}