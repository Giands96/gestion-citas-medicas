package com.trello.auth.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//? Definir el DTO para registro y definiremos las reglas de validación
public record RegisterRequest(
        @NotBlank(message = "El campo nombres no puede estar vacío")
        @Size(max = 50, message = "El campo nombres no puede tener más de 50 caracteres")
        String nombres,
        @NotBlank(message = "El campo apellidos no puede estar vacío")
        @Size(max = 50, message = "El campo apellidos no puede tener más")
        String apellidos,
        @NotBlank(message = "El campo email no puede estar vacío")
        @Size(max = 100, message = "El campo email no puede tener más de 100 caracteres")
        String email,
        @NotBlank(message = "El campo password no puede estar vacío")
        @Size(min = 8, max = 50, message = "El campo password debe tener al menos 8 caracteres")
        String password
) {

}
