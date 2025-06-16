package com.orchestrator.orchestrator.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.orchestrator.dto.ReservationDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.service.SagaReservationService;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ReservationSagaListener {

    private final SagaReservationService orchestratorService;
    private final SagaStateManager sagaStateManager;
    @Autowired
    private ObjectMapper objectMapper;

    public ReservationSagaListener(
            SagaReservationService orchestratorService,
            SagaStateManager sagaStateManager) {
        this.orchestratorService = orchestratorService;
        this.sagaStateManager = sagaStateManager;
    }

    // Criação de reserva

    @RabbitListener(queues = "reserva.criacao.sucesso")
    public void onReservaSucesso(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORCHESTRATOR] Serviço " + origin + " confirmou SUCESSO (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            // Controle de fluxo da saga (milhas -> voo -> reserva -> atualizar_milhas)
            if ("MILHAS".equals(origin)) {
                orchestratorService.onMilesSuccess(correlationId, message.getPayload());
            } else if ("VOO".equals(origin)) {
                orchestratorService.onFlightSuccess(correlationId, message.getPayload());
            } else if ("RESERVA".equals(origin)) {
                orchestratorService.onBookingSuccess(correlationId, message.getPayload());
            } else if ("ATUALIZAR_MILHAS".equals(origin)) {
                orchestratorService.onUpdateMilesSuccess(correlationId, message.getPayload());
            }

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORCHESTRATOR] SAGA CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    orchestratorService.onSagaFailure(correlationId, message.getPayload());
                } else {
                    System.out.println("[ORCHESTRATOR] SAGA CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "reserva.criacao.falhou")
    public void onReservaFalhou(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORCHESTRATOR] Serviço " + origin + " FALHOU (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);
            
            // Captura informações detalhadas do erro se disponíveis
            if (message.getErrorInfo() != null) {
                sagaStateManager.setErrorInfo(correlationId, message.getErrorInfo());
            }

            orchestratorService.onSagaFailure(correlationId, message.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cancelamento de reserva

    @RabbitListener(queues = "reserva.cancelamento.sucesso")
    public void onCancelamentoSucesso(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORCHESTRATOR] Serviço " + origin + " confirmou SUCESSO no cancelamento (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            // Controle de fluxo da saga de cancelamento (cancelar_reserva -> devolver_milhas)
            if ("CANCELAR_RESERVA".equals(origin)) {
                orchestratorService.onCancelBookingSuccess(correlationId, message.getPayload());
            } else if ("DEVOLVER_MILHAS".equals(origin)) {
                orchestratorService.onReturnMilesSuccess(correlationId, message.getPayload());
            }

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORCHESTRATOR] SAGA DE CANCELAMENTO CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    orchestratorService.onSagaFailure(correlationId, message.getPayload());
                } else {
                    System.out.println("[ORCHESTRATOR] SAGA DE CANCELAMENTO CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "reserva.cancelamento.falhou")
    public void onCancelamentoFalhou(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORCHESTRATOR] Serviço " + origin + " FALHOU no cancelamento (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);
            
            // Captura informações detalhadas do erro se disponíveis
            if (message.getErrorInfo() != null) {
                sagaStateManager.setErrorInfo(correlationId, message.getErrorInfo());
            }

            orchestratorService.onSagaFailure(correlationId, message.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
