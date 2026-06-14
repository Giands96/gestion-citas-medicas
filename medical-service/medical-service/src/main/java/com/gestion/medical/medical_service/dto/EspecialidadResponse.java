package com.gestion.medical.medical_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EspecialidadResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activa;
    private LocalDateTime createdAt;
}
