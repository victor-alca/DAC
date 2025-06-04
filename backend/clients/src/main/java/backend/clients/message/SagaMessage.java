package backend.clients.message;

import java.util.UUID;

import backend.clients.models.Client;


public class SagaMessage {
    private String correlationId;
    private String origin;
    private Client payload;
    private String operation;

    public SagaMessage() {
        this.correlationId = UUID.randomUUID().toString();
    }

    public SagaMessage(Client payload) {
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

    public Client getPayload() {
        return payload;
    }

    public void setPayload(Client payload) {
        this.payload = payload;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}