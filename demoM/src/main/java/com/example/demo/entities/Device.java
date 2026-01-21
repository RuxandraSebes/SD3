package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private UUID deviceId;

    @Column
    private Long userId;

    @Column
    private Double maxConsumption;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements;

    public Device() {
    }

    public Device(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}