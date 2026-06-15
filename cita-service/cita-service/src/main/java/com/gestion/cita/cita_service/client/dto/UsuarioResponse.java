package com.gestion.cita.cita_service.client.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String rol;
}
