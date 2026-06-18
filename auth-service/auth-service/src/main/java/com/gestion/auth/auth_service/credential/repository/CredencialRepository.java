package com.gestion.auth.auth_service.credential.repository;


import com.gestion.auth.auth_service.credential.entity.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    Optional<Credencial> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByUserId(Long userId);
}