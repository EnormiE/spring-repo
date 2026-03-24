package org.example.carrent;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;

public class Authentication {
    IUserRepository userRepository;
    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User authenticate(String login, String password) {
        User u = userRepository.getUser(login);
        if (u == null) return null;
        if (u.getPassword().equals(hashPassword(password))) {
            return u;
        }
        return null;
    }

    public User register(String login, String password, String role) {
        User user = new User(login, hashPassword(password), Role.valueOf(role.toUpperCase()));
        for (User u : userRepository.getUsers()) {
            if (Objects.equals(u.getLogin(), user.getLogin())) {
                return null;
            }
        }
        userRepository.add(user);
        return user;
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
