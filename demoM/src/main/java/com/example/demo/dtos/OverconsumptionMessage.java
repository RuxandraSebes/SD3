package com.example.demo.dtos;

import java.util.UUID;

public class OverconsumptionMessage {
    private Long userId;
    private UUID deviceId;
    private Double consumption;
    private Double maxLimit;
    private String timestamp;

    public OverconsumptionMessage() {
    }

    public OverconsumptionMessage(Long userId, UUID deviceId, Double consumption, Double maxLimit) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.consumption = consumption;
        this.maxLimit = maxLimit;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public Double getConsumption() {
        return consumption;
    }

    public void setConsumption(Double consumption) {
        this.consumption = consumption;
    }

    public Double getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(Double maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
