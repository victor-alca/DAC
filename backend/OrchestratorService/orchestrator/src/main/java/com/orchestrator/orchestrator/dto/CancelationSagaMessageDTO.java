package com.orchestrator.orchestrator.dto;

import java.util.Map;

import com.orchestrator.orchestrator.enums.CancelationSagaStep;

public class CancelationSagaMessageDTO {
    private String sagaId;
    private String flightId;
    private CancelationSagaStep step;
    private String status; // SUCCESS or FAILURE
    private Map<String, Object> payload;
    public String getSagaId() {
        return sagaId;
    }
    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }
    public String getFlightId() {
        return flightId;
    }
    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
    public CancelationSagaStep getStep() {
        return step;
    }
    public void setStep(CancelationSagaStep step) {
        this.step = step;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Map<String, Object> getPayload() {
        return payload;
    }
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
    
}
