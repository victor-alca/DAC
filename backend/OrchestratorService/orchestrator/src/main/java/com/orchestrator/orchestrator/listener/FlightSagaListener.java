package com.orchestrator.orchestrator.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.orchestrator.dto.FlightDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.service.SagaFlightService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class FlightSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SagaFlightService sagaFlightService;

    @RabbitListener(queues = "voo.cancelamento.sucesso")
    public void onFlightCancellationSuccess(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            String correlationId = message.getCorrelationId();
            FlightDTO payload = message.getPayload();
            
            System.out.println("[ORCHESTRATOR] Voo cancelado com sucesso. correlationId: " + correlationId);
            sagaFlightService.onFlightCancellationSuccess(correlationId, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "voo.cancelamento.reservas.sucesso")
    public void onFlightReservationsCancellationSuccess(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            String correlationId = message.getCorrelationId();
            FlightDTO payload = message.getPayload();
            
            System.out.println("[ORCHESTRATOR] Reservas do voo canceladas com sucesso. correlationId: " + correlationId);
            sagaFlightService.onFlightReservationsCancellationSuccess(correlationId, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "voo.cancelamento.falhou")
    public void onFlightCancellationFailure(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            String correlationId = message.getCorrelationId();
            FlightDTO payload = message.getPayload();
            
            System.out.println("[ORCHESTRATOR] Falha no cancelamento do voo. correlationId: " + correlationId);
            sagaFlightService.onSagaFailure(correlationId, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "voo.cancelamento.reservas.falhou")
    public void onFlightReservationsCancellationFailure(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            String correlationId = message.getCorrelationId();
            FlightDTO payload = message.getPayload();
            
            System.out.println("[ORCHESTRATOR] Falha no cancelamento das reservas do voo. correlationId: " + correlationId);
            sagaFlightService.onSagaFailure(correlationId, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}