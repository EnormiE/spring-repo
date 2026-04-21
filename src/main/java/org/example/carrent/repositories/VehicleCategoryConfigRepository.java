package org.example.carrent.repositories;

import org.example.carrent.models.VehicleCategoryConfig;

import java.util.List;
import java.util.Optional;

public interface VehicleCategoryConfigRepository {
    List<VehicleCategoryConfig> findAll();
    Optional<VehicleCategoryConfig> findByCategory(String category);
}

