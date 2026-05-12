package org.example.carrent.services;

import org.example.carrent.HibernateConfig;
import org.example.carrent.models.Role;
import org.example.carrent.models.User;
import org.example.carrent.repositories.impl.UserHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class UserHibernateService implements UserServiceInterface {

    private final UserHibernateRepository userRepo;
    private final RentalServiceInterface rentalService;

    public UserHibernateService(UserHibernateRepository userRepo, RentalServiceInterface rentalService) {
        this.userRepo = userRepo;
        this.rentalService = rentalService;
    }

    private void setSession(Session session) {
        userRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }

    @Override
    public List<User> findAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findAll();
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return userRepo.findById(id);
        }
    }

    @Override
    public void deleteUser(String toDeleteId, String whoRequestedId) throws Exception {
        Optional<User> toDeleteUserOpt = findById(toDeleteId);
        if (toDeleteUserOpt.isEmpty()) {
            return;
        }

        if (toDeleteUserOpt.get().getRole().equals(Role.ADMIN)) {
            if (findAllUsers().stream().filter(user -> user.getRole().equals(Role.ADMIN)).count() < 2) {
                throw new Exception("W systemie musi zostać CONAJMNIEJ 1 administrator");
            }
        }

        if (rentalService.findActiveRentalByUserId(toDeleteId).isPresent()) {
            throw new Exception("Nie można usunąć użytkownika, który  ma wypożyczony pojazd");
        }

        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);

            if (toDeleteId.equals(whoRequestedId)) {
                userRepo.deleteById(toDeleteId);
            } else {
                Optional<User> whoRequestedUserOpt = userRepo.findById(whoRequestedId);
                if (whoRequestedUserOpt.isPresent() && whoRequestedUserOpt.get().getRole().equals(Role.ADMIN)) {
                    userRepo.deleteById(toDeleteId);
                }
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }
}