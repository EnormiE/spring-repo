package org.example.carrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class UserRepository implements IUserRepository{

    UserRepository() {
        load();
    }

    List<User> userList;

    @Override
    public User getUser(String login) {
        for (User u : userList) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> tmpList = new ArrayList<>();
        for (User u: userList) {
            tmpList.add(new User(u));
        }
        return tmpList;
    }


    @Override
    public boolean update(User user) {
        User userCopy = new User(user);
//        user.setRentedVehicleId();
        User u = getUser(user.getLogin());
        if(u == null) return false;
//        u.setRentedVehicleId(user.getRentedVehicleId());
        userList.remove(u);
        userList.add(userCopy);
        save();
        return true;
    }

    private void save() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("users.csv"))) {
            for (User u : userList) {
                writer.print(u.getLogin() + ";");
                writer.print(u.getPassword() + ";");
                writer.print(u.getRole() + ";");
                if (u.getRentedVehicleId() != null) {
                    writer.println(u.getRentedVehicleId() + ";");
                }
                else writer.println();
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        String pathName = "users.csv";
        userList = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(pathName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            String[] linijka = scanner.nextLine().split(";");
            if (linijka.length == 3) {
                User u = new User(linijka[0], linijka[1], Role.valueOf(linijka[2]));
                userList.add(u);
            }
            else if (linijka.length == 4) {
                User u = new User(linijka[0], linijka[1], Role.valueOf(linijka[2]), linijka[3]);
                userList.add(u);
            }
        }
        scanner.close();

    }
}
