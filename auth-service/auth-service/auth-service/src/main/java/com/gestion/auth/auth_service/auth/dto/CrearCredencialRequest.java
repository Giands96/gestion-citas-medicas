package com.gestion.auth.auth_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record CrearCredencialRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        String rol
) {
}