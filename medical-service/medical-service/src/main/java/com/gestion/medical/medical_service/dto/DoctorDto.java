package com.gestion.medical.medical_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorDto {

    private Long id;
    private Long usuarioId;
    private Long especialidadId;
    private String cmp;
    private Boolean disponible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}