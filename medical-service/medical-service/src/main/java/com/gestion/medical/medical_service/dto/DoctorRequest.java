package com.gestion.medical.medical_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorRequest {
    @NotNull
    private Long usuarioId;

    @NotNull
    private Long especialidadId;

    @NotBlank @Size(max = 50)
    private String cmp;

    private Boolean disponible;
}
