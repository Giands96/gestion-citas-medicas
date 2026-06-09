package com.gestion.medical.medical_service.dto;

import lombok.Data;

@Data
public class EspecialidadDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activa;
}