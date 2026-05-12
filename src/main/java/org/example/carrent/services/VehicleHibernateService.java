package org.example.carrent.services;

import org.example.carrent.HibernateConfig;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.impl.VehicleHibernateRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleHibernateService implements VehicleServiceInterface {

    private final VehicleValidator vehicleValidator;
    private final VehicleHibernateRepository vehicleRepo;
    private final RentalServiceInterface rentalService;

    public VehicleHibernateService(VehicleValidator vehicleValidator, VehicleHibernateRepository vehicleRepo, RentalServiceInterface rentalService) {
        this.vehicleValidator = vehicleValidator;
        this.vehicleRepo = vehicleRepo;
        this.rentalService = rentalService;
    }

    private void setSession(Session session) {
        vehicleRepo.setSession(session);
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return findAllVehicles().stream()
                .filter(vehicle -> !isVehicleRented(vehicle.getId()))
                .toList();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findById(id);
        }
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);
            Vehicle saved = vehicleRepo.save(vehicle);
            tx.commit();
            return saved;
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public void removeVehicle(String vehicleId) throws Exception {
        if (isVehicleRented(vehicleId)) {
            throw new Exception("Nie można usunąć pojazdu, który jest wypożyczony");
        }
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            setSession(session);
            vehicleRepo.deleteById(vehicleId);
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            throw e;
        }
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalService.vehicleHasActiveRental(vehicleId);
    }
}