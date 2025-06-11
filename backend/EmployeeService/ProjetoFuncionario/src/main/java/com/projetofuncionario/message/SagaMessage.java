package com.projetofuncionario.message;

import java.util.Map;

public class SagaMessage<T> {
    private String correlationId;
    private String origin;
    private T payload;
    private String operation;
    private Map<String, Object> errorInfo;

    public Map<String, Object> getErrorInfo() {
        return errorInfo;
    }
    
    public void setErrorInfo(Map<String, Object> errorInfo) {
        this.errorInfo = errorInfo;
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
