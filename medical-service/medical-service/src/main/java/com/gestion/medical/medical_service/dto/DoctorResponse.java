package com.gestion.medical.medical_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DoctorResponse {
    private Long id;
    private Long usuarioId;
    private Long especialidadId;
    private String especialidadNombre;
    private String cmp;
    private Boolean disponible;
    private LocalDateTime createdAt;
}
