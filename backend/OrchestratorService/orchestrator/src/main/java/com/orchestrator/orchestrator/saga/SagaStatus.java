package com.orchestrator.orchestrator.saga;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SagaStatus {
    private final Set<String> expectedServices;
    private final Set<String> respondedServices = new HashSet<>();
    private final Set<String> failedServices = new HashSet<>();

    public SagaStatus(Set<String> expectedServices) {
        this.expectedServices = new HashSet<>(expectedServices);
    }

    public void markSuccess(String serviceName) {
        respondedServices.add(serviceName);
    }

    public void markFailure(String serviceName) {
        failedServices.add(serviceName);
        respondedServices.add(serviceName);
    }

    public boolean hasServiceResponded(String serviceName) {
        return respondedServices.contains(serviceName);
    }

    public boolean isComplete() {
        return respondedServices.containsAll(expectedServices);
    }

    public boolean hasFailure() {
        return !failedServices.isEmpty();
    }

    public Set<String> getPendingServices() {
        Set<String> pending = new HashSet<>(expectedServices);
        pending.removeAll(respondedServices);
        return pending;
    }

    public Set<String> getRespondedServices() {
        return Collections.unmodifiableSet(respondedServices);
    }

    public Set<String> getExpectedServices() {
        return Collections.unmodifiableSet(expectedServices);
    }

    public Set<String> getFailedServices() {
        return failedServices;
    }

    public Set<String> getSuccessfulServices() {
        Set<String> successful = new HashSet<>(respondedServices);
        successful.removeAll(failedServices);
        return Collections.unmodifiableSet(successful);
    }
}
