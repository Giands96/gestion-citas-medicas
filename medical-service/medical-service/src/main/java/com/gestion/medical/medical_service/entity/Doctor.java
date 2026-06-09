package com.gestion.medical.medical_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "doctores", schema = "doctor_service")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @Column(name = "especialidad_id", nullable = false)
    private Long especialidadId;

    @Column(name = "cmp", nullable = false, unique = true)
    private String cmp;

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}