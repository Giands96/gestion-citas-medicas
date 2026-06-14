package com.gestion.medical.medical_service.service;

import com.gestion.medical.medical_service.dto.EspecialidadRequest;
import com.gestion.medical.medical_service.dto.EspecialidadResponse;
import java.util.List;

public interface EspecialidadService {
    EspecialidadResponse crear(EspecialidadRequest request);
    EspecialidadResponse obtenerPorId(Long id);
    List<EspecialidadResponse> listarTodas();
    EspecialidadResponse actualizar(Long id, EspecialidadRequest request);
    void eliminar(Long id);
}
