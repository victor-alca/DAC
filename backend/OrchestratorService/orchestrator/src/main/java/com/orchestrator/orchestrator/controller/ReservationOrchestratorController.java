package com.orchestrator.orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.service.SagaReservationService;
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
}
