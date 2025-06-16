package com.orchestrator.orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.service.SagaReservationService;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestBody;
import com.orchestrator.orchestrator.dto.ReservationDTO;
@RestController
@RequestMapping("/api/orchestrator/reservation")
public class ReservationOrchestratorController {

    @Autowired
    private SagaReservationService service;

    @PostMapping
    public ResponseEntity<?> criarReserva(@RequestBody ReservationDTO reservationDTO) {
        String correlationId = service.startReservationSaga(reservationDTO);
        return ResponseEntity.accepted().body(Map.of(
                "message", "Reserva iniciada com sucesso",
                "correlationId", correlationId));
    }

    @DeleteMapping("/reservas/{codigoReserva}")
    public ResponseEntity<Map<String, String>> cancelarReserva(@PathVariable String codigoReserva) {
        // Cria um DTO com apenas o código da reserva para cancelamento
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setCodigo_reserva(codigoReserva);
        
        String correlationId = service.startCancellationSaga(reservationDTO);
        
        Map<String, String> response = new HashMap<>();
        response.put("correlationId", correlationId);
        response.put("status", "CANCELLATION_STARTED");
        response.put("codigoReserva", codigoReserva);
        response.put("message", "Cancelamento de reserva iniciado com sucesso");
        
        return ResponseEntity.accepted().body(response);
    }
}
