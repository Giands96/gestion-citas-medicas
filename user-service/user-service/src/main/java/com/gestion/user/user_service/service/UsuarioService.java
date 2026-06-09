package com.gestion.user.user_service.service;

import com.gestion.user.user_service.dto.UsuarioDto;
import java.util.List;

public interface UsuarioService {

    List<UsuarioDto> getAllUsuarios();

    UsuarioDto getUsuarioById(Long id);

    UsuarioDto createUsuario(UsuarioDto usuarioDto);

    UsuarioDto updateUsuario(Long id, UsuarioDto usuarioDto);

    void deleteUsuario(Long id);
}