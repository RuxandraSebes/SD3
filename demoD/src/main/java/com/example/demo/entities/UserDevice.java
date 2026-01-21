package com.example.demo.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "user_device")
public class UserDevice {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID idRelatie;

    @Column(nullable = false)
    private UUID idUser;

    @Column(nullable = false)
    private UUID idDevice;

    public UserDevice() {}

    public UserDevice(UUID idUser, UUID idDevice) {
        this.idUser = idUser;
        this.idDevice = idDevice;
    }

    public UUID getIdRelatie() {
        return idRelatie;
    }

    public void setIdRelatie(UUID idRelatie) {
        this.idRelatie = idRelatie;
    }

    public UUID getIdUser() {
        return idUser;
    }

    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }

    public UUID getIdDevice() {
        return idDevice;
    }

    public void setIdDevice(UUID idDevice) {
        this.idDevice = idDevice;
    }
}
