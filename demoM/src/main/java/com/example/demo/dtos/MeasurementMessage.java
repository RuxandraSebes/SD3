package com.example.demo.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class MeasurementMessage {
    private UUID deviceId;
    private Double value;
    private LocalDateTime timestamp;

    public MeasurementMessage() {}

    public MeasurementMessage(UUID deviceId, Double value, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.value = value;
        this.timestamp = timestamp;
    }


    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}