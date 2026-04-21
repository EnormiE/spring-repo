package org.example.carrent.services;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public User addUser(User user) {
        userRepository.save(user);
        return user;
    }
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public void deleteUser(String toDeleteId, String whoRequestedId) throws Exception {
        // jesli jedyny admin, to nie moze sie usunac
        if (findById(toDeleteId).get().getRole().equals(Role.ADMIN)) {
            if (findAllUsers().stream().filter(user -> user.getRole().equals(Role.ADMIN)).count() < 2) {
                throw new Exception("W systemie musi zostać CONAJMNIEJ 1 administrator");
            }
        }
        // nie kasuj użytkownika, który ma wypożyczony pojazd
        if (rentalService.findActiveRentalByUserId(toDeleteId).isPresent()) {
            throw new Exception("Nie można usunąć użytkownika, który  ma wypożyczony pojazd");
        }
        // sam siebie mozesz usunac
        if (toDeleteId.equals(whoRequestedId)) {
            userRepository.deleteById(toDeleteId);
            // TO DO: wyloguj użytkownika
        }
        else {
            // admin moze usuwac innych
            if (findById(whoRequestedId).get().getRole().equals(Role.ADMIN)) {
                userRepository.deleteById(toDeleteId);
            }
            return;
        }
    }
}
