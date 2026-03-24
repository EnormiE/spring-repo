package org.example.carrent;

public class Car extends Vehicle{
    public Car(String id, String brand, String model, int year, float price, boolean rented) {
        super(id, brand, model, year, price, rented);
    }

    public Car(Car car) {
        super(car);
    }

    public Car copyVehicle() {
        return new Car(this);
    }
}
