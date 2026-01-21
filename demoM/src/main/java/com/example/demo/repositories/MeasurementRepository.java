package com.example.demo.repositories;

import com.example.demo.entities.Measurement;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Measurement m WHERE m.device.deviceId=:deviceId")
    void deleteByDeviceId(@Param("deviceId") UUID deviceId);

    @Query("""
        SELECT m FROM Measurement m
        WHERE m.device.deviceId = :deviceId
        AND m.timestamp BETWEEN :start AND :end
        ORDER BY m.timestamp
    """)
    List<Measurement> findByDeviceIdAndTimestampBetween(
            @Param("deviceId") UUID deviceId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT m FROM Measurement m
        WHERE m.device.deviceId = :deviceId
        ORDER BY m.timestamp DESC
    """)
    List<Measurement> findByDeviceIdOrderByTimestampDesc(@Param("deviceId") UUID deviceId);
}
