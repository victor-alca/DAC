package com.orchestrator.orchestrator.controller;

import com.orchestrator.orchestrator.service.ReservationOrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orchestrator/reservation")
public class ReservationOrchestratorController {

    private final ReservationOrchestratorService service;

    @Autowired
    public ReservationOrchestratorController(ReservationOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/start-saga")
    public ResponseEntity<String> startSaga(@RequestParam String reservationId) {
        service.startSaga(reservationId);
        return ResponseEntity.ok("Saga de criação de reserva iniciada para ID: " + reservationId);
    }
}
