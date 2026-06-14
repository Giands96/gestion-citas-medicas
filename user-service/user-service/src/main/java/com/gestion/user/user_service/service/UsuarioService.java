package com.gestion.user.user_service.service;

import com.gestion.user.user_service.dto.request.UsuarioRequest;
import com.gestion.user.user_service.dto.response.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    UsuarioResponse crearUsuario(UsuarioRequest request);
    UsuarioResponse obtenerPorId(Long id);
    List<UsuarioResponse> listarTodos();
    UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request);
    void eliminarUsuario(Long id);
}
