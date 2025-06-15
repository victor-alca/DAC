package com.booking.auth.auth.message;

import java.util.UUID;

import com.booking.auth.auth.DTO.UserDTO;

public class SagaMessage {
    private String correlationId;
    private String origin;
    private UserDTO payload;
    private String operation;

    public SagaMessage() {
        this.correlationId = UUID.randomUUID().toString();
    }

    public SagaMessage(UserDTO payload) {
        this();
        this.payload = payload;
    }

    // Getters e setters
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public UserDTO getPayload() {
        return payload;
    }

    public void setPayload(UserDTO payload) {
        this.payload = payload;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
