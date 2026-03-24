package org.example.carrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository {

    private List<Vehicle> vehicleList;

    public VehicleRepositoryImpl() {
        this.vehicleList = new ArrayList<>();
        load();
    }

    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v : vehicleList) {
            if (v.getId().equals(id)) {
                if (v.isRented()) {
                    return false;
                }
                else {
                    v.setRented(true);
                    save();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle v : vehicleList) {
            if (v.getId().equals(id)) {
                if (v.isRented()) {
                    v.setRented(false);
                    save();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> tmpList = new ArrayList<>();
        for (Vehicle v: vehicleList) {
            tmpList.add(v.copyVehicle());
        }
        return tmpList;
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicleList) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        for (Vehicle v : vehicleList) {
            if (v.getId().equals(vehicle.getId())) {
                return false;
            }
        }
        vehicleList.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        for (Vehicle v : vehicleList) {
            if (v.getId().equals(id)) {
                vehicleList.remove(v);
                save();
                return true;
            }
        }
        return false;
    }

    private void save() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("vehicles.csv"))) {
            for (Vehicle v : vehicleList) {
                writer.println(v.toCSV());
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void load() {
        String pathName = "vehicles.csv";
        vehicleList = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(pathName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
//        scanner.useDelimiter(";");
//        while(scanner.hasNext()){
//            System.out.print(scanner.next()+"|");
//        }
        while (scanner.hasNextLine()) {
            String[] linijka = scanner.nextLine().split(";");
            if (Objects.equals(linijka[0], "CAR")) {
                Car car = new Car(linijka[1], linijka[2], linijka[3], Integer.parseInt(linijka[4]), Float.parseFloat(linijka[5]), Boolean.parseBoolean(linijka[6]));
                vehicleList.add(car);
            }
            else if (Objects.equals(linijka[0], "MOTORCYCLE")) {
                Motorcycle motorcycle = new Motorcycle(linijka[1], linijka[2], linijka[3], Integer.parseInt(linijka[4]), Float.parseFloat(linijka[5]), Boolean.parseBoolean(linijka[6]), MotorcycleCategory.valueOf(linijka[7]));
                vehicleList.add(motorcycle);
            }
        }
        scanner.close();

    }
}
