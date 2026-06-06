package com.trello.auth.auth_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        //! Tiempo en que ocurrió el error
        LocalDateTime timestamp,
        //! Código de estado HTTP (e.g., 400, 404, 500)
        int estado,
        //! Mensaje del Error
        String mensaje,
        //! La ruta que causó el error.
        String ruta,

        //! Mapeamos los errores para que el cliente pueda entender qué campos fallaron y por qué
        Map<String, String> ErroresValidacion
) {
}
