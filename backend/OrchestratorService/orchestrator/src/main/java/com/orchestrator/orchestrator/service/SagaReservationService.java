package com.orchestrator.orchestrator.service;

import com.orchestrator.orchestrator.dto.ReservationDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SagaReservationService {
    private final RabbitTemplate rabbitTemplate;
    private final SagaStateManager sagaStateManager;

    public SagaReservationService(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaStateManager = sagaStateManager;
    }

    // Inicia a SAGA de reserva: publica em reserva.criacao.iniciada (milhas)
    public String startReservationSaga(ReservationDTO reservationDTO) {
        SagaMessage<ReservationDTO> sagaMessage = new SagaMessage<>(reservationDTO);
        String correlationId = sagaMessage.getCorrelationId();

        // Espera sucesso de: MILHAS, VOO, RESERVA (ordem controlada pelo orchestrator)
        sagaStateManager.createSaga(correlationId, Set.of("MILHAS", "VOO", "RESERVA"));

        // Primeiro passo: só milhas consome
        rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.iniciada", sagaMessage);

        System.out.println("[SAGA] Iniciando reserva com correlationId: " + correlationId);

        return correlationId;
    }

    // Chame este método quando receber sucesso das milhas
    public void onMilesSuccess(String correlationId, ReservationDTO payload) {
        sagaStateManager.markSuccess(correlationId, "MILHAS");
        // Próximo passo: voo
        SagaMessage<ReservationDTO> sagaMessage = new SagaMessage<>(payload);
        sagaMessage.setCorrelationId(correlationId);
        rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.iniciada.voo", sagaMessage);
        System.out.println("[SAGA] Milhas OK, enviando para VOO. correlationId: " + correlationId);
    }

    // Chame este método quando receber sucesso do voo
    public void onFlightSuccess(String correlationId, ReservationDTO payload) {
        sagaStateManager.markSuccess(correlationId, "VOO");
        // Próximo passo: reserva
        SagaMessage<ReservationDTO> sagaMessage = new SagaMessage<>(payload);
        sagaMessage.setCorrelationId(correlationId);
        rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.iniciada.reserva", sagaMessage);
        System.out.println("[SAGA] Voo OK, enviando para RESERVA. correlationId: " + correlationId);
    }

    // Chame este método quando receber sucesso da reserva
    public void onBookingSuccess(String correlationId, ReservationDTO payload) {
        sagaStateManager.markSuccess(correlationId, "RESERVA");
        System.out.println("[SAGA] Reserva criada com sucesso. Saga COMPLETED_SUCCESS. correlationId: " + correlationId);
    }

    // Chame este método quando receber falha de qualquer serviço
    public void onSagaFailure(String correlationId, ReservationDTO payload) {
        System.out.println("[SAGA] Falha detectada, iniciando compensação. correlationId: " + correlationId);
        Set<String> servicosComSucesso = sagaStateManager.get(correlationId).getSuccessfulServices();

        for (String service : servicosComSucesso) {
            SagaMessage<ReservationDTO> compensacaoMessage = new SagaMessage<>(payload);
            compensacaoMessage.setCorrelationId(correlationId);
            compensacaoMessage.setOrigin("ORCHESTRATOR");
            compensacaoMessage.setOperation("COMPENSATE");

            String routingKey = switch (service) {
                case "MILHAS" -> "reserva.milhas.compensar";
                case "VOO" -> "reserva.voo.compensar";
                case "RESERVA" -> "reserva.reserva.compensar";
                default -> null;
            };
            if (routingKey != null) {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", routingKey, compensacaoMessage);
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