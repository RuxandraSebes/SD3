package com.example.demo.controllers;

import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.security.UserAuthInfo;
import com.example.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDetailsDTO>> getDevices(
            @AuthenticationPrincipal UserAuthInfo userAuthInfo
    ) {
        return ResponseEntity.ok(deviceService.findAllDeviceDetails(userAuthInfo));
    }

    @PostMapping
    public ResponseEntity<DeviceDetailsDTO> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);
        DeviceDetailsDTO saved = deviceService.findDeviceById(id);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO>updateDevice(@PathVariable UUID id,@Valid @RequestBody DeviceDetailsDTO device){
        DeviceDetailsDTO updatedDevice = deviceService.update(id, device);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id){
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/user/{personId}")
    public ResponseEntity<Void> deleteUserDevices(@PathVariable("personId") UUID personId) {
        deviceService.deleteUserDeviceAssociations(personId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}