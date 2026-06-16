package org.example.carrent.web;

import org.example.carrent.models.User;
import org.example.carrent.services.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    // tylko dla admina
    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    // dla usera
    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();

        User user = userService.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + login);
        }

        return ResponseEntity.ok(user);
    }

    // tylko dla admina
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable String id) {
        return ResponseEntity.of(userService.findById(id));
    }
}