package com.orchestrator.orchestrator.saga;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SagaStateManager {
    private final Map<String, SagaStatus> sagaMap = new ConcurrentHashMap<>();

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
    }
}