package com.gestion.cita.cita_service.client.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private Long usuarioId;
    private Long especialidadId;
    private String especialidadNombre;
    private String cmp;
    private Boolean disponible;
}
