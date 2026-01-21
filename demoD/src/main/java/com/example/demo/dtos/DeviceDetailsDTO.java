package com.example.demo.dtos;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;

    public int getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(int maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "location is required")
    private String location;

    @NotNull(message = "maxConsumption is required")
    @Min(value = 0, message = "Maximum consumption cannot be negative")
    private int maxConsumption;

    private List<UUID> assignedUserIds;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(UUID id, String name, String location, int maxConsumption, List<UUID> assignedUserIds) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxConsumption = maxConsumption;
        this.assignedUserIds = assignedUserIds;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String address) {
        this.location = address;
    }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         DeviceDetailsDTO that = (DeviceDetailsDTO) o;
         return maxConsumption == that.maxConsumption &&
                 Objects.equals(name, that.name) &&
                 Objects.equals(location, that.location) &&
                 Objects.equals(assignedUserIds, that.assignedUserIds);
     }

    public List<UUID> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(List<UUID> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, maxConsumption, assignedUserIds);
    }

}
