package org.example.carrent;

import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;
import org.example.carrent.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository() {
            List<User> users = new ArrayList<>();
            @Override public List<User> findAll() { return users; }
            @Override public Optional<User> findById(String id) { return Optional.empty(); }
            @Override public Optional<User> findByLogin(String login) {
                return users.stream().filter(u -> u.getLogin().equals(login)).findFirst();
            }
            @Override public User save(User user) { users.add(user); return user; }
            @Override public void deleteById(String id) {}
        };
        authService = new AuthService(userRepository);

        authService.register("admin", "admin123");
    }

    @Test
    void shouldAuthenticateUserWithCorrectLoginAndPassword() {
        Optional<User> userOpt = authService.login("admin", "admin123");

        assertTrue(userOpt.isPresent());
        assertEquals("admin", userOpt.get().getLogin());
    }

    @Test
    void shouldNotAuthenticateUserWithWrongPassword() {
        Optional<User> userOpt = authService.login("admin", "zlehaslo");

        assertFalse(userOpt.isPresent());
    }

    @Test
    void shouldNotAuthenticateNonExistingUser() {
        Optional<User> userOpt = authService.login("brak", "admin123");

        assertFalse(userOpt.isPresent());
    }

    @Test
    void hashPasswordShouldValidateCorrectly() {
        String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());

        assertTrue(BCrypt.checkpw("admin123", hash));
    }
}