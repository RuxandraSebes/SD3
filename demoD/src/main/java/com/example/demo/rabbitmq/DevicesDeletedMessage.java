package com.example.demo.rabbitmq;

import java.util.List;
import java.util.UUID;

public class DevicesDeletedMessage {

    private UUID deviceId;

    public DevicesDeletedMessage() {}

    public DevicesDeletedMessage(UUID deviceIds) {
        this.deviceId = deviceIds;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }
}
