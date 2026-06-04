package com.gestion.cita.cita_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitaDto {
    private Long id;
    private Long pacienteId;
    private Long doctorId;
    private LocalDate fecha;

    private LocalTime hora;

    private String motivo;
    private String estado;
}
