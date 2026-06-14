package com.gestion.medical.medical_service.repository;

import com.gestion.medical.medical_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByEspecialidadId(Long especialidadId);
    List<Doctor> findByDisponibleTrue();
    boolean existsByUsuarioId(Long usuarioId);
}
