package com.orchestrator.orchestrator.dto;

import java.util.Set;

public class SagaStatusResponse {
    private String status; // IN_PROGRESS, COMPLETED_SUCCESS, COMPLETED_ERROR
    private Set<String> respondedServices;
    private Set<String> failedServices;
    private Set<String> pendingServices;

    public SagaStatusResponse(String status, Set<String> respondedServices, Set<String> failedServices, Set<String> pendingServices) {
        this.status = status;
        this.respondedServices = respondedServices;
        this.failedServices = failedServices;
        this.pendingServices = pendingServices;
    }

    public String getStatus() {
        return status;
    }

    public Set<String> getRespondedServices() {
        return respondedServices;
    }

    public Set<String> getFailedServices() {
        return failedServices;
    }

    public Set<String> getPendingServices() {
        return pendingServices;
    }
}