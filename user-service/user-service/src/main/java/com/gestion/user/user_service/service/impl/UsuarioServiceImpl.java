package com.gestion.user.user_service.service.impl;

import com.gestion.user.user_service.dto.request.UsuarioRequest;
import com.gestion.user.user_service.dto.response.UsuarioResponse;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.enums.TipoUsuario;
import com.gestion.user.user_service.exception.DuplicateResourceException;
import com.gestion.user.user_service.exception.ResourceNotFoundException;
import com.gestion.user.user_service.repository.UsuarioRepository;
import com.gestion.user.user_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final ModelMapper mapper;

    @Override
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        Usuario usuario = mapper.map(request, Usuario.class);
        Usuario guardado = repository.save(usuario);

        return mapper.map(guardado, UsuarioResponse.class);
    }


    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapper.map(usuario, UsuarioResponse.class);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = repository.findAll();
        return usuarios.stream()
                .map(usuario -> mapper.map(usuario, UsuarioResponse.class))
                .toList();
    }

    @Override
    public UsuarioResponse actualizarUsuario(Long id, UsuarioRequest request) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        mapper.map(request, usuario);
        Usuario updated = repository.save(usuario);
        return  mapper.map(updated, UsuarioResponse.class);
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setActivo(false);
        repository.save(usuario);
    }
}
