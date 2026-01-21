package com.example.demo.dtos.builders;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getName(),
                deviceDetailsDTO.getLocation(),
                deviceDetailsDTO.getMaxConsumption());
    }
}
