package com.orchestrator.orchestrator.enums;

public enum ReservationSagaStep {
    START_RESERVATION,
    RESERVATION_CREATED,
    FLIGHT_UPDATED,
    CLIENT_UPDATED,
    COMPLETED,

    // Passos de compensação
    ROLLBACK_CLIENT,
    ROLLBACK_FLIGHT,
    ROLLBACK_RESERVATION,
    FAILED
}
