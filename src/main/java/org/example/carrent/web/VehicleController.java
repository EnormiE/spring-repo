package org.example.carrent.web;

import org.example.carrent.models.Vehicle;
import org.example.carrent.services.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleServiceInterface vehicleService;

    public VehicleController(VehicleServiceInterface vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available) {
        return available ? vehicleService.findAvailableVehicles() : vehicleService.findAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> get(@PathVariable String id) {
        return ResponseEntity.of(vehicleService.findById(id));
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws Exception {
        vehicleService.removeVehicle(id);
        return ResponseEntity.noContent().build();
    }
}