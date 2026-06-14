package com.gestion.cita.cita_service.service;

import java.util.List;

import com.gestion.cita.cita_service.dto.CitaRequest;
import com.gestion.cita.cita_service.dto.CitaResponse;

public interface CitaService {
    CitaResponse createCita(CitaRequest request);
    CitaResponse updateCita(Long id, CitaRequest request);
    CitaResponse getCitaById(Long id);
    void deleteCita(Long id);
    List<CitaResponse> getAllCitas();
}
