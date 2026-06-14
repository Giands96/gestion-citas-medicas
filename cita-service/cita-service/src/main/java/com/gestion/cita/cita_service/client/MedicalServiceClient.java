package com.gestion.cita.cita_service.client;

import com.gestion.cita.cita_service.client.dto.DoctorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "medical-service", path = "/api/medicals/doctores")
public interface MedicalServiceClient {
    @GetMapping("/{id}")
    DoctorResponse obtenerDoctorPorId(@PathVariable("id") Long id);
}
