package com.example.simulator.service;

import com.example.simulator.dto.MeasurementMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class SimulationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // CRITICAL FIX: Use the correct queue name
    @Value("${rabbitmq.queue.measurements:queueMeasurements}")
    private String MEASUREMENTS_QUEUE;

    public void startSimulation(UUID deviceId) {
        System.out.println("=".repeat(70));
        System.out.println("Starting Energy Consumption Simulation (24h)");
        System.out.println("=".repeat(70));
        System.out.println("Device ID: " + deviceId);
        System.out.println("Queue: " + MEASUREMENTS_QUEUE);
        System.out.println("10 minutes = 3 seconds → 24h simulation");
        System.out.println("=".repeat(70));

        Random random = new Random();

        // Start time
        LocalDateTime currentTime = LocalDateTime.now();

        // 24 hours / 10 minutes = 144 measurements
        int totalMeasurements = 24 * 6;
        int measurementCount = 0;

        while (measurementCount < totalMeasurements) {
            try {
                // Wait 0.1 seconds (simulate 10 minutes)
                Thread.sleep(1000);

                // Advance simulated time
                currentTime = currentTime.plusMinutes(10);

                // Calculate consumption
                double consumption = calculateConsumption(currentTime, random);

                MeasurementMessage data = new MeasurementMessage(
                        deviceId,
                        consumption,
                        currentTime);

                // Send to RabbitMQ
                rabbitTemplate.convertAndSend(MEASUREMENTS_QUEUE, data);
                measurementCount++;

                // Log output
                String timePeriod = getTimePeriod(currentTime.getHour());
                System.out.printf(
                        "[%s] %s | Value: %6.2f kWh | Sent: %d/%d%n",
                        currentTime.toLocalTime().toString().substring(0, 8),
                        timePeriod,
                        consumption,
                        measurementCount,
                        totalMeasurements);

            } catch (InterruptedException e) {
                System.out.println("\nSimulation interrupted.");
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                System.err.println("Error sending data: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("\nSimulation finished: 24h of data sent successfully.");
    }

    private double calculateConsumption(LocalDateTime time, Random random) {
        int hour = time.getHour();

        double baseLoad;

        // Realistic daily consumption patterns
        if (hour >= 0 && hour < 6) {
            // Night: Low consumption (0.5 - 1.0 kWh per 10 min = 3-6 kW)
            baseLoad = 0.5 + (random.nextDouble() * 0.5);
        } else if (hour >= 6 && hour < 9) {
            // Morning peak (2.0 - 3.0 kWh per 10 min = 12-18 kW)
            baseLoad = 2.0 + (random.nextDouble() * 1.0);
        } else if (hour >= 9 && hour < 17) {
            // Day: Moderate (1.0 - 2.0 kWh per 10 min = 6-12 kW)
            baseLoad = 1.0 + (random.nextDouble() * 1.0);
        } else if (hour >= 17 && hour < 22) {
            // Evening peak (3.0 - 5.0 kWh per 10 min = 18-30 kW)
            baseLoad = 3.0 + (random.nextDouble() * 2.0);
        } else {
            // Late night (1.0 - 1.5 kWh per 10 min = 6-9 kW)
            baseLoad = 1.0 + (random.nextDouble() * 0.5);
        }

        // Add small fluctuations
        double fluctuation = (random.nextDouble() - 0.5) * 0.2;
        return Math.max(0, baseLoad + fluctuation);
    }

    private String getTimePeriod(int hour) {
        if (hour >= 6 && hour < 9)
            return "MORNING";
        else if (hour >= 9 && hour < 17)
            return "DAY    ";
        else if (hour >= 17 && hour < 22)
            return "EVENING";
        else
            return "NIGHT  ";
    }
}