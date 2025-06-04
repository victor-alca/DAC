package com.orchestrator.orchestrator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orchestrator.orchestrator.dto.SagaStatusResponse;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import com.orchestrator.orchestrator.saga.SagaStatus;

@RestController
@RequestMapping("/saga")
public class SagaController {
    private final SagaStateManager sagaStateManager;

    public SagaController(SagaStateManager sagaStateManager) {
        this.sagaStateManager = sagaStateManager;
    }

    @GetMapping("/{correlationId}")
    public ResponseEntity<?> consultarStatus(@PathVariable String correlationId) {
        SagaStatus sagaStatus = sagaStateManager.get(correlationId);

        if (sagaStatus == null) {
            return ResponseEntity.notFound().build();
        }

        String status;
        if (!sagaStatus.isComplete()) {
            status = "IN_PROGRESS";
        } else if (sagaStatus.hasFailure()) {
            status = "COMPLETED_ERROR";
        } else {
            status = "COMPLETED_SUCCESS";
        }

        SagaStatusResponse response = new SagaStatusResponse(
                status,
                sagaStatus.getRespondedServices(),
                sagaStatus.getFailedServices(),
                sagaStatus.getPendingServices());

        return ResponseEntity.ok(response);
    }
}