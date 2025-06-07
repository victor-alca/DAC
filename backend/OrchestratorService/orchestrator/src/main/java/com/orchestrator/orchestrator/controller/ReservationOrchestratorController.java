package com.orchestrator.orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.service.ReservationOrchestratorService;

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
