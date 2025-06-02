package com.flight.rest;

import com.flight.dto.AeroportoDTO;
import com.flight.model.Airport;
import com.flight.service.AirportService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/aeroportos")
public class AirportREST {

    private final AirportService airportService;

    public AirportREST(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public ResponseEntity<List<AeroportoDTO>> getAeroportos() {
        List<AeroportoDTO> aeroportos = airportService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        if (aeroportos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(aeroportos);
    }

    private AeroportoDTO toDTO(Airport airport) {
        AeroportoDTO dto = new AeroportoDTO();
        dto.setCodigo(airport.getCode());
        dto.setNome(airport.getName());
        dto.setCidade(airport.getCity());
        dto.setUf(airport.getFederativeUnit());
        return dto;
    }
}