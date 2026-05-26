package org.example.carrent.repositories.impl;

import org.example.carrent.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("jpa")
public interface UserJpaRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String login);
}