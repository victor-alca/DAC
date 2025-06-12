package com.orchestrator.orchestrator.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.orchestrator.orchestrator.dto.FlightDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;

@Service
public class SagaFlightCancelationService {

    private final RabbitTemplate rabbitTemplate;
    private final SagaStateManager sagaStateManager;

    public SagaFlightCancelationService(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaStateManager = sagaStateManager;
    }

    /**
     * Inicia a saga de voo enviando a primeira mensagem para o serviço de voos.
     * Gera um sagaId único para rastreamento.
     * ainda precisa fazer uma saga para cancelar e outra para confirmar (RF13/RF14)
     */
    public String startCancelationSaga(FlightDTO flightDTO) {
        SagaMessage<FlightDTO> sagaMessage = new SagaMessage<>(flightDTO);
        String correlationId = sagaMessage.getCorrelationId();

        // Espera sucesso de: VOO, RESERVAS (ordem controlada pelo orchestrator)
        sagaStateManager.createSaga(correlationId, Set.of("VOO", "RESERVAS", "MILHAS"));

        // Primeiro passo: só voo é cancelado
        rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.iniciada", sagaMessage);

        System.out.println("[SAGA] Iniciando cancelamento de voo com correlationId: " + correlationId);

        return correlationId;
    }


    // Chame este método quando receber sucesso do voo
    public void onFlightSuccess(String correlationId, FlightDTO payload) {
        sagaStateManager.markSuccess(correlationId, "VOO");
        // Próximo passo: voo
        SagaMessage<FlightDTO> sagaMessage = new SagaMessage<>(payload);
        sagaMessage.setCorrelationId(correlationId);
        rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.iniciada.atualizar.reservas", sagaMessage);
        System.out.println("[SAGA] Voo cancelado, enviando para RESERVAS. correlationId: " + correlationId);
    }

    // Chame este método quando receber sucesso da reserva
    public void onBookingSuccess(String correlationId, FlightDTO payload) {
        sagaStateManager.markSuccess(correlationId, "RESERVA");
        // Próximo passo: atualizar milhas
        SagaMessage<FlightDTO> sagaMessage = new SagaMessage<>(payload);
        sagaMessage.setCorrelationId(correlationId);
        rabbitTemplate.convertAndSend("reserva.saga.exchange", "voo.cancelamento.iniciada.atualizar.milhas", sagaMessage);
        System.out.println("[SAGA] Reserva OK, enviando para ATUALIZAR_MILHAS. correlationId: " + correlationId);
    }

    // Chame este método quando receber o update de milhas for bem-sucedido
    public void onUpdateMilesSuccess(String correlationId, FlightDTO payload) {
        sagaStateManager.markSuccess(correlationId, "ATUALIZAR_MILHAS");
        System.out.println("[SAGA] Atualização de milhas concluída. Saga COMPLETED_SUCCESS. correlationId: " + correlationId);
        
        // Armazena o código do voo como resultado
        if (payload.getCodigo_voo() != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("codigoVoo", payload.getCodigo_voo());
            result.put("type", "reservation");
            
            sagaStateManager.setResult(correlationId, result);
            System.out.println("[SAGA] Código do voo armazenado: " + payload.getCodigo_voo());
        }
    }

    

    // Chame este método quando receber falha de qualquer serviço
    public void onSagaFailure(String correlationId, FlightDTO payload) {
        System.out.println("[SAGA] Falha detectada, iniciando compensação. correlationId: " + correlationId);
        Set<String> servicosComSucesso = sagaStateManager.get(correlationId).getSuccessfulServices();

        for (String service : servicosComSucesso) {
            SagaMessage<FlightDTO> compensacaoMessage = new SagaMessage<>(payload);
            compensacaoMessage.setCorrelationId(correlationId);
            compensacaoMessage.setOrigin("ORCHESTRATOR");
            compensacaoMessage.setOperation("COMPENSATE");

            String routingKey = switch (service) {
                case "VOO" -> "voo.cancelamento.compensar";
                case "RESERVAS" -> "voo.cancelamento.reservas.compensar";
                case "MILHAS" -> "voo.cancelamento.milhas.compensar";
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
