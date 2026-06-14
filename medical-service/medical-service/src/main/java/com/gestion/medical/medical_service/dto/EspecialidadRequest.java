package com.gestion.medical.medical_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EspecialidadRequest {
    @NotBlank @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;
}
