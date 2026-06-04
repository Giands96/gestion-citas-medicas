package com.gestion.cita.cita_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.cita.cita_service.entity.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
}
