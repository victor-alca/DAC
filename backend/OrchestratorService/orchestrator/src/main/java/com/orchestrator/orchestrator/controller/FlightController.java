package com.orchestrator.orchestrator.controller;

import com.orchestrator.orchestrator.dto.FlightDTO;
import com.orchestrator.orchestrator.service.SagaFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/saga/voos")
public class FlightController {

    @Autowired
    private SagaFlightService service;

    @PostMapping("/{codigoVoo}/cancelar")
    public ResponseEntity<Map<String, String>> cancelarVoo(@PathVariable String codigoVoo, @RequestBody Map<String, String> body) {
        // Cria um DTO com o código do voo e estado para cancelamento
        FlightDTO flightCancellationDTO = new FlightDTO();
        flightCancellationDTO.setCodigo_voo(codigoVoo);
        flightCancellationDTO.setEstado(body.get("estado"));
        
        String correlationId = service.startFlightCancellationSaga(flightCancellationDTO);
        
        Map<String, String> response = new HashMap<>();
        response.put("correlationId", correlationId);
        response.put("status", "FLIGHT_CANCELLATION_STARTED");
        response.put("codigoVoo", codigoVoo);
        response.put("message", "Cancelamento de voo iniciado com sucesso");
        
        return ResponseEntity.accepted().body(response);
    }
}