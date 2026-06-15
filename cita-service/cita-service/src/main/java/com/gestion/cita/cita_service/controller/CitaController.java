package com.gestion.cita.cita_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.cita.cita_service.dto.CitaRequest;
import com.gestion.cita.cita_service.dto.CitaResponse;
import com.gestion.cita.cita_service.errors.ErrorResponse;
import com.gestion.cita.cita_service.errors.NoResponse;
import com.gestion.cita.cita_service.service.CitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<CitaResponse> createCita(@Valid @RequestBody CitaRequest request) {
        CitaResponse response = citaService.createCita(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponse> updateCita(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        CitaResponse response = citaService.updateCita(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCitaById(@PathVariable Long id) {
        try {
            CitaResponse response = citaService.getCitaById(id);
            return ResponseEntity.ok(response);
        } catch (NoResponse e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage(), "NOT_FOUND"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {
        citaService.deleteCita(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CitaResponse>> getAllCitas() {
        List<CitaResponse> citas = citaService.getAllCitas();
        return ResponseEntity.ok(citas);
    }
}
