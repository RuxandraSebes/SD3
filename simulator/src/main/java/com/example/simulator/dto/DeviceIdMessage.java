package com.example.simulator.dto;

import java.util.UUID;

public class DeviceIdMessage {

    private UUID deviceId;

    public DeviceIdMessage() {}

    public DeviceIdMessage(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }
}
