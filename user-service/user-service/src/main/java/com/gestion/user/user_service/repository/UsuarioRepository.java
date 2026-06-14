package com.gestion.user.user_service.repository;

import com.gestion.user.user_service.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario,Long>{
    Optional<Usuario> findByNombres(String nombres);
}
