package com.gestion.cita.cita_service.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CitaResponse {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteApellido;
    private Long doctorId;
    private String doctorNombre;
    private String doctorApellido;
    private Long especialidadId;
    private String especialidadNombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private String estado;
}
