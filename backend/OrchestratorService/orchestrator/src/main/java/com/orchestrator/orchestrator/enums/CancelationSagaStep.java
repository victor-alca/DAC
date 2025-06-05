package com.orchestrator.orchestrator.enums;

public enum CancelationSagaStep {
    START_CANCELATION,
    FLIGHT_CANCELLED,
    BOOKINGS_UPDATED,
    COMPLETED,

    // Passos de compensação
    ROLLBACK_CLIENT,
    ROLLBACK_FLIGHT,
    ROLLBACK_RESERVATION,
    FAILED
}
