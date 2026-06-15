package com.gestion.user.user_service.dto.response;

import com.gestion.user.user_service.enums.Rol;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Boolean activo;
    private Rol rol;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
