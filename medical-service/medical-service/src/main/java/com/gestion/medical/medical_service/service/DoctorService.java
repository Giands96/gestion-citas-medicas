package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.DoctorRequest;
import com.gestion.medical.medical_service.dto.DoctorResponse;
import java.util.List;

public interface DoctorService {
    DoctorResponse crear(DoctorRequest request);
    DoctorResponse obtenerPorId(Long id);
    List<DoctorResponse> listarTodos();
    List<DoctorResponse> listarPorEspecialidad(Long especialidadId);
    DoctorResponse actualizar(Long id, DoctorRequest request);
    void eliminar(Long id);
}
