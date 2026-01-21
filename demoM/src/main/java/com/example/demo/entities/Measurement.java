package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "measurements", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id, timestamp")
})
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Measurement() {}

    public Measurement(Device device, Double value, LocalDateTime timestamp) {
        this.device = device;
        this.value = value;
        this.timestamp = timestamp;
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}