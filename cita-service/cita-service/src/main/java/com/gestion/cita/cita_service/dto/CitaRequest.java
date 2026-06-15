package com.gestion.cita.cita_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class CitaRequest {
    @NotNull
    private Long pacienteId;

    @NotNull
    private Long doctorId;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private LocalTime hora;

    @Size(max = 255)
    private String motivo;

    @Size(max = 50)
    private String estado;
}
