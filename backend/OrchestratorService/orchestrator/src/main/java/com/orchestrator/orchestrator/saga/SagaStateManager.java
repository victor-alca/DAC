package com.orchestrator.orchestrator.saga;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SagaStateManager {
    private final Map<String, SagaStatus> sagaMap = new ConcurrentHashMap<>();
    private final Map<String, Object> sagaResults = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> sagaErrors = new ConcurrentHashMap<>(); // NOVO

    public void createSaga(String correlationId, Set<String> expectedServices) {
        sagaMap.putIfAbsent(correlationId, new SagaStatus(expectedServices));
    }

    public SagaStatus get(String correlationId) {
        return sagaMap.get(correlationId);
    }

    public void markSuccess(String correlationId, String service) {
        var saga = sagaMap.get(correlationId);
        if (saga != null) saga.markSuccess(service);
    }

    public boolean isComplete(String correlationId) {
        var saga = sagaMap.get(correlationId);
        return saga != null && saga.isComplete();
    }

    public void clear(String correlationId) {
        sagaMap.remove(correlationId);
        sagaResults.remove(correlationId);
        sagaErrors.remove(correlationId);
    }

    public void setResult(String correlationId, Object result) {
        sagaResults.put(correlationId, result);
    }

    public Object getResult(String correlationId) {
        return sagaResults.get(correlationId);
    }

    public void setErrorInfo(String correlationId, Map<String, Object> errorInfo) {
        sagaErrors.put(correlationId, errorInfo);
    }

    public Map<String, Object> getErrorInfo(String correlationId) {
        return sagaErrors.get(correlationId);
    }

}
