package com.gestion.cita.cita_service.client;

import com.gestion.cita.cita_service.client.dto.UsuarioResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UsuarioResponse obtenerUsuarioPorId(@PathVariable("id") Long id);
}
