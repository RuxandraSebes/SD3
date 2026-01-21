package com.example.simulator;

import com.example.simulator.service.SimulationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

@SpringBootApplication
public class DeviceSimulatorApplication implements CommandLineRunner {

    private final SimulationService simulationService;

    public DeviceSimulatorApplication(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DeviceSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting simulations from deviceIds.txt");

        try {
            ClassPathResource resource = new ClassPathResource("deviceIds.txt");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream()))) {

                reader.lines()
                        .filter(line -> !line.isBlank())
                        .map(UUID::fromString)
                        .forEach(deviceId -> {
                            System.out.println("Launching simulation for device: " + deviceId);

                            // THREAD PER DEVICE
                            new Thread(() -> simulationService.startSimulation(deviceId)).start();
                        });
            }

        } catch (Exception e) {
            System.err.println("Failed to start simulations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
