package com.orchestrator.orchestrator.controller;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> getSagaStatus(@PathVariable String correlationId) {
        SagaStatus sagaStatus = sagaStateManager.get(correlationId);
        
        if (sagaStatus == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (sagaStatus.isComplete()) {
            if (sagaStatus.hasFailure()) {
                response.put("status", "COMPLETED_ERROR");
                response.put("failedServices", sagaStatus.getFailedServices());
            } else {
                response.put("status", "COMPLETED_SUCCESS");
                // Adiciona dados do resultado (ex: código da reserva)
                Object result = sagaStateManager.getResult(correlationId);
                if (result != null) {
                    response.put("result", result);
                }
            }
        } else {
            response.put("status", "IN_PROGRESS");
            response.put("completedServices", sagaStatus.getSuccessfulServices());
        }
        
        return ResponseEntity.ok(response);
    }
}