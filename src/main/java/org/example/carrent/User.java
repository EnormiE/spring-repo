package org.example.carrent;

public class User {
    //- login
    //- password (hash hasła)
    //- role
    //- rentedVehicleId (identyfikator wypożyczonego pojazdu)
    private String login;
    private String password;
    private Role role;
    private String rentedVehicleId;

    public User(String login, String password, Role role, String rentedVehicleId) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = null;
    }

    public User(User user) {
        this.login = user.getLogin();
        this.rentedVehicleId = user.getRentedVehicleId();
        this.role = user.getRole();
        this.password = user.getPassword();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setRentedVehicleId(String rentedVehicleId) {
        this.rentedVehicleId = rentedVehicleId;
    }

    @Override
    public String toString() {
        return
                "login: " + login + " | " +
                "password: " + password + " | " +
                "role: " + role + " | " +
                "rentedVehicleId: " + rentedVehicleId;
    }
}
