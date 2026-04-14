package org.example.carrent;

import org.example.carrent.models.Rental;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.RentalRepository;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleRepository;
import org.example.carrent.services.AuthService;
import org.example.carrent.services.RentalService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class UserInterface {

    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final RentalService rentalService;
    private final Scanner scanner;

    public UserInterface(VehicleRepository vehicleRepo, UserRepository userRepo, RentalRepository rentalRepo,
                         AuthService authService, RentalService rentalService) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
        this.rentalService = rentalService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n--- MENU GŁÓWNE ---");
            System.out.println("1. Zaloguj");
            System.out.println("2. Zarejestruj");
            System.out.println("0. Wyjście");
            System.out.print("Wybierz opcję: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "0":
                    System.out.println("Zamykanie aplikacji...");
                    return;
                default:
                    System.out.println("Nieprawidłowy wybór!");
            }
        }
    }

    private void handleLogin() {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Hasło: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = authService.login(login, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == Role.USER) {
                userMenu(user);
            } else if (user.getRole() == Role.ADMIN) {
                adminMenu(user);
            }
        } else {
            System.out.println("Błąd logowania: Nieprawidłowy login lub hasło.");
        }
    }

    private void handleRegister() {
        System.out.print("Podaj nowy login: ");
        String login = scanner.nextLine();
        System.out.print("Podaj nowe hasło: ");
        String password = scanner.nextLine();

        if (authService.register(login, password)) {
            System.out.println("Rejestracja zakończona sukcesem! Możesz się teraz zalogować.");
        } else {
            System.out.println("Błąd: Użytkownik o takim loginie już istnieje.");
        }
    }

    private void userMenu(User user) {
        while (true) {
            System.out.println("\nWitaj " + user.getLogin() + " (USER)!");
            System.out.println("1. Wypożycz pojazd");
            System.out.println("2. Zwróć pojazd");
            System.out.println("3. Wyświetl swoje dane i aktualne wypożyczenie");
            System.out.println("4. Pokaż dostępne pojazdy");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz opcję: ");
            String select = scanner.nextLine();

            switch (select) {
                case "1":
                    System.out.print("Podaj ID pojazdu do wypożyczenia: ");
                    String idToRent = scanner.nextLine();
                    if (rentalService.rentVehicle(idToRent, user.getId())) {
                        System.out.println("Sukces: Wypożyczono pojazd!");
                    } else {
                        System.out.println("Niepowodzenie: Pojazd nie istnieje, jest zajęty, lub wystąpił błąd.");
                    }
                    break;
                case "2":
                    System.out.print("Podaj ID pojazdu do zwrotu: ");
                    String idToReturn = scanner.nextLine();
                    if (rentalService.returnVehicle(idToReturn)) {
                        System.out.println("Sukces: Zwrócono pojazd!");
                    } else {
                        System.out.println("Niepowodzenie: Brak aktywnego wypożyczenia dla tego ID.");
                    }
                    break;
                case "3":
                    System.out.println("--- TWOJE DANE ---");
                    System.out.println(user.toString());
                    System.out.println("--- TWOJE AKTYWNE WYPOŻYCZENIA ---");
                    rentalRepo.findAll().stream()
                            .filter(r -> r.getUserId().equals(user.getId()) && r.isActive())
                            .forEach(r -> {
                                System.out.println(r.toString());
                                vehicleRepo.findById(r.getVehicleId())
                                        .ifPresent(v -> System.out.println("  -> Pojazd: " + v.toString()));
                            });
                    break;
                case "4":
                    System.out.println("--- DOSTĘPNE POJAZDY ---");
                    for (Vehicle v : vehicleRepo.findAll()) {
                        if (rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty()) {
                            System.out.println(v.toString());
                        }
                    }
                    break;
                case "0":
                    return;
            }
        }
    }

    private void adminMenu(User user) {
        while (true) {
            System.out.println("\nWitaj " + user.getLogin() + " (ADMIN)!");
            System.out.println("1. Dodaj pojazd");
            System.out.println("2. Usuń pojazd");
            System.out.println("3. Przeglądaj wszystkie pojazdy");
            System.out.println("4. Przeglądaj użytkowników");
            System.out.println("0. Wyloguj");
            System.out.print("Wybierz opcję: ");
            String select = scanner.nextLine();

            switch (select) {
                case "1":
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Kategoria (np. CAR, MOTORCYCLE): ");
                    String category = scanner.nextLine().toUpperCase();
                    System.out.print("Marka: ");
                    String brand = scanner.nextLine();
                    System.out.print("Model: ");
                    String model = scanner.nextLine();
                    System.out.print("Rok: ");
                    int year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Cena: ");
                    double price = Double.parseDouble(scanner.nextLine());

                    Map<String, Object> attributes = new HashMap<>();
                    if (category.equals("MOTORCYCLE")) {
                        System.out.print("Kategoria prawa jazdy (np. A, A1): ");
                        attributes.put("kategoria", scanner.nextLine());
                    }

                    Vehicle v = Vehicle.builder()
                            .id(id)
                            .category(category)
                            .brand(brand)
                            .model(model)
                            .year(year)
                            .price(price)
                            .attributes(attributes)
                            .build();

                    vehicleRepo.save(v);
                    System.out.println("Sukces: Dodano pojazd.");
                    break;
                case "2":
                    System.out.print("Podaj ID pojazdu do usunięcia: ");
                    String delId = scanner.nextLine();
                    vehicleRepo.deleteById(delId);
                    System.out.println("Wykonano (jeśli istniał, został usunięty).");
                    break;
                case "3":
                    System.out.println("--- WSZYSTKIE POJAZDY ---");
                    for (Vehicle veh : vehicleRepo.findAll()) {
                        String status = rentalRepo.findByVehicleIdAndReturnDateIsNull(veh.getId()).isPresent() ? "[WYPOŻYCZONY]" : "[DOSTĘPNY]";
                        System.out.println(status + " " + veh.toString() + " Atrybuty: " + veh.getAttributes());
                    }
                    break;
                case "4":
                    System.out.println("--- UŻYTKOWNICY ---");
                    for (User u : userRepo.findAll()) {
                        System.out.println(u.toString());
                    }
                    break;
                case "0":
                    return;
            }
        }
    }
}