package org.example.carrent;

public abstract class Vehicle {
    private String id;
    private String brand;
    private String model;
    private int year;
    private float price;
    private boolean rented;

    public Vehicle(String id, String brand, String model, int year, float price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public Vehicle(Vehicle vehicle) {
        this.id = vehicle.id;
        this.brand = vehicle.brand;
        this.model = vehicle.model;
        this.year = vehicle.year;
        this.price = vehicle.price;
        this.rented = vehicle.rented;
    }

    public abstract Vehicle copyVehicle();

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public float getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String toCSV() {
        return getClass().getSimpleName().toUpperCase() + ";" + id + ";" + brand + ";" + model + ";" + year  + ";" + price + ";" + rented;
    }

    @Override
    public String toString() {
        return  "id: " + id + " | type: " + getClass().getSimpleName() +  " | brand: " + brand + " | model: " + model + " | year: " + year  + " | price: " + price + " | rented: " + rented;
    }
}
