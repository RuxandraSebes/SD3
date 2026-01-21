
package com.example.demo.controllers;

import com.example.demo.dtos.MeasurementMessage;
import com.example.demo.service.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/monitoring")
public class MeasurementController {

    private final MonitoringService monitoringService;

    public MeasurementController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/devices/{deviceId}/measurements")
    public ResponseEntity<List<MeasurementMessage>> getDeviceMeasurements(
            @PathVariable UUID deviceId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;

        List<MeasurementMessage> measurements = monitoringService.findMeasurementsByDeviceId(deviceId, start, end);
        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/devices/uuids")
    public ResponseEntity<List<UUID>> getAllDeviceUuids() {
        List<UUID> uuids = monitoringService.findAllDeviceUuids();
        return ResponseEntity.ok(uuids);
    }
}