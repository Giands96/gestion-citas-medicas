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

import com.gestion.cita.cita_service.dto.CitaDto;
import com.gestion.cita.cita_service.errors.ErrorResponse;
import com.gestion.cita.cita_service.errors.NoResponse;
import com.gestion.cita.cita_service.service.CitaService;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<Void> createCita(@RequestBody CitaDto citaDto) {
        citaService.createCita(citaDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaDto> updateCita(@PathVariable Long id, @RequestBody CitaDto citaDto) {
        CitaDto updatedCita = citaService.updateCita(id, citaDto);
        return ResponseEntity.ok(updatedCita);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCitaById(@PathVariable Long id) {
        try {
            CitaDto citaDto = citaService.getCitaById(id);
            return ResponseEntity.ok(citaDto);
        } catch (NoResponse e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage(), "NOT_FOUND"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCita(@PathVariable Long id) {
        citaService.deleteCita(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CitaDto>> getAllCitas() {
        List<CitaDto> citas = citaService.getAllCitas();
        return ResponseEntity.ok(citas);
    }
}
