package com.orchestrator.orchestrator.message;

import java.util.UUID;

public class SagaMessage<T> {
    private String correlationId;
    private String origin;
    private T payload;
    private String operation;

    public SagaMessage() {
        this.correlationId = UUID.randomUUID().toString();
    }

    public SagaMessage(T payload) {
        this();
        this.payload = payload;
    }

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

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
