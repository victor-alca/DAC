package com.orchestrator.orchestrator.service;

import com.orchestrator.orchestrator.dto.FlightDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SagaFlightService {
    private final RabbitTemplate rabbitTemplate;
    private final SagaStateManager sagaStateManager;

    public SagaFlightService(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaStateManager = sagaStateManager;
    }

    // SAGA DE CANCELAMENTO DE VOO
    public String startFlightCancellationSaga(FlightDTO flightCancellationDTO) {
        SagaMessage<FlightDTO> sagaMessage = new SagaMessage<>(flightCancellationDTO);
        String correlationId = sagaMessage.getCorrelationId();

        // Espera sucesso de: CANCELAR_VOO, CANCELAR_RESERVAS_VOO (ordem controlada pelo orchestrator)
        sagaStateManager.createSaga(correlationId, Set.of("CANCELAR_VOO", "CANCELAR_RESERVAS_VOO"));

        // Primeiro passo: cancelar voo
        rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.iniciado", sagaMessage);
        System.out.println("[SAGA] Iniciando cancelamento de voo com correlationId: " + correlationId);

        return correlationId;
    }

    // Chame este método quando receber sucesso do cancelamento do voo
    public void onFlightCancellationSuccess(String correlationId, FlightDTO payload) {
        sagaStateManager.markSuccess(correlationId, "CANCELAR_VOO");
        // Próximo passo: cancelar reservas do voo
        SagaMessage<FlightDTO> sagaMessage = new SagaMessage<>(payload);
        sagaMessage.setCorrelationId(correlationId);
        rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.reservas.iniciado", sagaMessage);
        System.out.println("[SAGA] Voo cancelado OK, enviando para CANCELAR_RESERVAS_VOO. correlationId: " + correlationId);
    }

    // Chame este método quando receber sucesso do cancelamento das reservas do voo
    public void onFlightReservationsCancellationSuccess(String correlationId, FlightDTO payload) {
        sagaStateManager.markSuccess(correlationId, "CANCELAR_RESERVAS_VOO");
        System.out.println("[SAGA] Reservas do voo canceladas. Saga de cancelamento de voo COMPLETED_SUCCESS. correlationId: " + correlationId);
    }

    // Chame este método quando receber falha na saga de cancelamento de voo
    public void onSagaFailure(String correlationId, FlightDTO payload) {
        System.out.println("[SAGA] Falha detectada no cancelamento de voo, iniciando compensação. correlationId: " + correlationId);
        Set<String> servicosComSucesso = sagaStateManager.get(correlationId).getSuccessfulServices();

        for (String service : servicosComSucesso) {
            SagaMessage<FlightDTO> compensacaoMessage = new SagaMessage<>(payload);
            compensacaoMessage.setCorrelationId(correlationId);
            compensacaoMessage.setOrigin("ORCHESTRATOR");
            compensacaoMessage.setOperation("COMPENSATE");

            String routingKey = switch (service) {
                case "CANCELAR_VOO" -> "voo.cancelamento.compensar";
                case "CANCELAR_RESERVAS_VOO" -> "voo.reservas.cancelamento.compensar";
                default -> null;
            };
            if (routingKey != null) {
                rabbitTemplate.convertAndSend("voo.saga.exchange", routingKey, compensacaoMessage);
                System.out.println("[SAGA] Enviando COMPENSATE para serviço " + service);
            }
        }
    }

    // Retorna status da saga (IN_PROGRESS, COMPLETED_SUCCESS, COMPLETED_ERROR)
    public String getSagaStatus(String correlationId) {
        var saga = sagaStateManager.get(correlationId);
        if (saga == null) return "NOT_FOUND";
        if (saga.hasFailure()) return "COMPLETED_ERROR";
        if (saga.isComplete()) return "COMPLETED_SUCCESS";
        return "IN_PROGRESS";
    }
}