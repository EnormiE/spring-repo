package org.example.carrent.services;

import org.example.carrent.HibernateConfig;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {

    private final UserHibernateRepository userRepository;

    public AuthHibernateService(UserHibernateRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void setSession(Session session) {
        userRepository.setSession(session);
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }

    @Override
    public Optional<User> login(String login, String password) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            Optional<User> userOpt = userRepository.findByLogin(login);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean register(String login, String password) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (userRepository.findByLogin(login).isPresent()) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User newUser = User.builder()
                    .id(UUID.randomUUID().toString())
                    .login(login)
                    .passwordHash(hashedPassword)
                    .role(Role.USER)
                    .build();

            userRepository.save(newUser);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }
}