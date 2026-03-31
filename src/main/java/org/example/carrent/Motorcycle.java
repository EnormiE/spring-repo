package org.example.carrent;

import org.example.carrent.models.Vehicle;

public class Motorcycle extends Vehicle {
//    public enum Kategoria {A, A1, A2, AM, B};
    MotorcycleCategory kategoria;

    public Motorcycle(String id, String brand, String model, int year, float price, boolean rented, MotorcycleCategory kategoria) {
        super(id, brand, model, year, price, rented);
        this.kategoria = kategoria;
    }

    public Motorcycle(Motorcycle motorcycle) {
        super(motorcycle);
        this.kategoria = motorcycle.kategoria;
    }

    public Motorcycle copyVehicle() {
        return new Motorcycle(this);
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + kategoria.name();
    }

    public String toString() {
        return super.toString() + " | kategoria: " + kategoria.name();
    }
}
