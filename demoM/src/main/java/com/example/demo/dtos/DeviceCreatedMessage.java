package com.example.demo.dtos;

import java.util.UUID;

public class DeviceCreatedMessage {
    private UUID id;
    private Long userId;
    private Double maxConsumption;

    public DeviceCreatedMessage() {
    }

    public DeviceCreatedMessage(UUID id, Long userId, Double maxConsumption) {
        this.id = id;
        this.userId = userId;
        this.maxConsumption = maxConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}