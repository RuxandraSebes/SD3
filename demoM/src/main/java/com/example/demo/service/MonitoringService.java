package com.example.demo.service;

import com.example.demo.dtos.DeviceCreatedMessage;
import com.example.demo.dtos.MeasurementMessage;
import com.example.demo.entities.Device;
import com.example.demo.entities.Measurement;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.repositories.MeasurementRepository;
import com.example.demo.rabbitmq.RabbitMqMessageSender;
import com.example.demo.dtos.OverconsumptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private final DeviceRepository deviceRepository;
    private final MeasurementRepository measurementRepository;
    private final RabbitMqMessageSender rabbitMqMessageSender;

    @Autowired
    public MonitoringService(DeviceRepository deviceRepository,
            MeasurementRepository measurementRepository,
            RabbitMqMessageSender rabbitMqMessageSender) {
        this.deviceRepository = deviceRepository;
        this.measurementRepository = measurementRepository;
        this.rabbitMqMessageSender = rabbitMqMessageSender;
    }

    @Transactional
    public void registerDevice(DeviceCreatedMessage message) {
        Optional<Device> existing = deviceRepository.findById(message.getId());

        if (existing.isPresent()) {
            LOGGER.info("Device {} already exists in monitoring database", message.getId());
            Device device = existing.get();
            device.setUserId(message.getUserId());
            device.setMaxConsumption(message.getMaxConsumption());
            deviceRepository.save(device);
            return;
        }

        Device device = new Device(
                message.getId());
        device.setUserId(message.getUserId());
        device.setMaxConsumption(message.getMaxConsumption());

        deviceRepository.save(device);
        LOGGER.info("Registered new device {} in monitoring service", message.getId());
    }

    @Transactional
    public void deleteDeviceById(UUID deviceId) {
        deviceRepository.deleteById(deviceId);
        LOGGER.info("Deleted measurements for devices {}", deviceId);
    }

    public List<MeasurementMessage> findMeasurementsByDeviceId(UUID deviceId, LocalDateTime start, LocalDateTime end) {
        List<Measurement> measurements;

        if (start != null && end != null) {
            measurements = measurementRepository.findByDeviceIdAndTimestampBetween(deviceId, start, end);
        } else {

            measurements = measurementRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
        }

        return measurements.stream()
                .map(m -> new MeasurementMessage(m.getDevice().getDeviceId(), m.getValue(), m.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<UUID> findAllDeviceUuids() {
        return deviceRepository.findAll().stream()
                .map(Device::getDeviceId)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveMeasurement(MeasurementMessage message) {
        Optional<Device> deviceOpt = deviceRepository.findById(message.getDeviceId());

        if (deviceOpt.isEmpty()) {
            LOGGER.warn("Device {} not found in monitoring database. Skipping measurement.",
                    message.getDeviceId());
            return;
        }

        Device device = deviceOpt.get();

        if (device.getMaxConsumption() != null && message.getValue() > device.getMaxConsumption()) {
            LOGGER.warn("OVERCONSUMPTION detected for device {}: value={}, limit={}",
                    device.getDeviceId(), message.getValue(), device.getMaxConsumption());

            OverconsumptionMessage alert = new OverconsumptionMessage(
                    device.getUserId(),
                    device.getDeviceId(),
                    message.getValue(),
                    device.getMaxConsumption());
            rabbitMqMessageSender.sendOverconsumptionAlert(alert);
        }

        Measurement measurement = new Measurement(
                device,
                message.getValue(),
                message.getTimestamp());

        measurementRepository.save(measurement);
        LOGGER.debug("Saved measurement for device {}: value={}, timestamp={}",
                device.getDeviceId(), message.getValue(), message.getTimestamp());
    }
}