package org.example.carrent.services;

import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SimpleUserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final SimpleRentalService rentalService;

    public SimpleUserService(UserRepository userRepository, SimpleRentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public User addUser(User user) {
        user = userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public void deleteUser(String toDeleteId, String whoRequestedId) throws IllegalStateException {
        if (findById(toDeleteId).get().getRole().equals(Role.ADMIN)) {
            if (findAllUsers().stream().filter(user -> user.getRole().equals(Role.ADMIN)).count() < 2) {
                throw new IllegalStateException("W systemie musi zostać CONAJMNIEJ 1 administrator");
            }
        }
        if (rentalService.findActiveRentalByUserId(toDeleteId).isPresent()) {
            throw new IllegalStateException("Nie można usunąć użytkownika, który ma wypożyczony pojazd");
        }
        if (toDeleteId.equals(whoRequestedId)) {
            userRepository.deleteById(toDeleteId);
        } else {
            if (findById(whoRequestedId).get().getRole().equals(Role.ADMIN)) {
                userRepository.deleteById(toDeleteId);
            }
        }
    }
}