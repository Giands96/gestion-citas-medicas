package com.gestion.cita.cita_service.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "citas", schema = "appointment_service")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(length = 255)
    private String motivo;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String estado = "PENDIENTE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
