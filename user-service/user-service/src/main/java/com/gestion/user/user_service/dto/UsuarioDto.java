package com.gestion.user.user_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioDto {

    private Long id;
    private Long usuarioId;
    private String dni;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}