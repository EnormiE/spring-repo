package org.example.carrent;

import org.apache.commons.codec.digest.DigestUtils;

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

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
