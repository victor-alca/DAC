package com.orchestrator.orchestrator.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.orchestrator.dto.FlightDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import com.orchestrator.orchestrator.service.SagaFlightCancelationService;

@Component
public class FlightCancelationSagaListener {

    private final SagaFlightCancelationService orchestratorService;
        private final SagaStateManager sagaStateManager;
    @Autowired
    private ObjectMapper objectMapper;

    public FlightCancelationSagaListener(
            SagaFlightCancelationService orchestratorService,
            SagaStateManager sagaStateManager) {
        this.orchestratorService = orchestratorService;
        this.sagaStateManager = sagaStateManager;
    }

    @RabbitListener(queues = "voo.cancelamento.sucesso")
    public void onVooCancelamentoSucesso(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<FlightDTO>>() {});
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORCHESTRATOR] Serviço " + origin + " confirmou SUCESSO (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            // Controle de fluxo da saga (voo -> reservas)
            if ("VOO".equals(origin)) {
                orchestratorService.onFlightSuccess(correlationId, message.getPayload());
            } else if ("RESERVAS".equals(origin)) {
                orchestratorService.onBookingSuccess(correlationId, message.getPayload());
            }else if ("MILHAS".equals(origin)) {
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

    @RabbitListener(queues = "voo.cancelamento.falhou")
    public void onVooCancelamentoFalhou(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<FlightDTO>>() {});
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
}
