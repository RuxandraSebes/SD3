package com.example.demo.repositories;

import com.example.demo.entities.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByIdDevice(UUID idDevice);

    List<UserDevice> findByIdUser(UUID idUser);

    List<UserDevice> findAllByIdDevice(UUID idDevice);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserDevice ud WHERE ud.idUser = :personId")
    int deleteByPersonId(@Param("personId") UUID personId);

}
