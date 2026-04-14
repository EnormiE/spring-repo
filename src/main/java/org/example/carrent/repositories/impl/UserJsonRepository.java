package org.example.carrent.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.carrent.db.JsonFileStorage;
import org.example.carrent.models.User;
import org.example.carrent.repositories.UserRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserJsonRepository implements UserRepository {

    private final JsonFileStorage<User> storage;
    private final List<User> users;

    public UserJsonRepository() {
        Type type = new TypeToken<List<User>>(){}.getType();
        this.storage = new JsonFileStorage<>("users.json", type);
        this.users = storage.load();
    }

    @Override
    public List<User> findAll() {
        List<User> copy = new ArrayList<>();
        for (User user : users) {
            copy.add(user.copy());
        }
        return copy;
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public User save(User user) {
        User copy = user.copy();
        users.removeIf(u -> u.getId().equals(copy.getId()));
        users.add(copy);
        storage.save(users);
        return copy;
    }

    @Override
    public void deleteById(String id) {
        users.removeIf(user -> user.getId().equals(id));
        storage.save(users);
    }
}