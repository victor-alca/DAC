package com.orchestrator.orchestrator.dto;

import java.util.Map;

import com.orchestrator.orchestrator.enums.EmployeeSagaStep;
import com.orchestrator.orchestrator.enums.ReservationSagaStep;

public class SagaMessageDTO {
    private String sagaId;
    private String reservationId;
    private ReservationSagaStep step;
    private EmployeeSagaStep employeeStep;
    private String status; // SUCCESS or FAILURE
    private Map<String, Object> payload;
    
    public String getSagaId() {
        return sagaId;
    }
    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }
    public String getReservationId() {
        return reservationId;
    }
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    public ReservationSagaStep getStep() {
        return step;
    }
    public void setStep(ReservationSagaStep step) {
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
