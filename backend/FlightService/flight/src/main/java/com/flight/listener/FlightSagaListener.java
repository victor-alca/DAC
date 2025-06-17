package com.flight.listener;

import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.message.SagaMessage;
import com.flight.service.FlightService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.flight.dto.FlightDTO;
import java.util.Map;

@Component
public class FlightSagaListener {
   
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FlightService flightService;

    @RabbitListener(queues = "voo.cancelamento.iniciado.voo")
    public void onCancelFlightSaga(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            
            FlightDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();

            System.out.println("[CANCELAR_VOO] Processando cancelamento do voo " + dto.getCodigo_voo());

            // Cancela o voo
            boolean success = flightService.cancelFlight(dto.getCodigo_voo());
            message.setOrigin("CANCELAR_VOO");

            if (success) {
                String jsonResponse = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.sucesso", jsonResponse);
                System.out.println("[CANCELAR_VOO] Voo " + dto.getCodigo_voo() + " cancelado com sucesso na SAGA");
            } else {
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.falhou", jsonError);
                System.out.println("[CANCELAR_VOO] Falha ao cancelar voo " + dto.getCodigo_voo() + " na SAGA");
            }
        } catch (ResponseStatusException e) {
            System.err.println("[CANCELAR_VOO] Falha na SAGA - " + e.getStatusCode() + ": " + e.getReason());
            
            try {
                SagaMessage<FlightDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<FlightDTO>>() {}
                );
                message.setOrigin("CANCELAR_VOO");
                
                // Adiciona informações do erro na mensagem
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("errorCode", e.getStatusCode().value());
                errorInfo.put("errorMessage", e.getReason());
                message.setErrorInfo(errorInfo);
                
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.falhou", jsonError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("[CANCELAR_VOO] Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            try {
                SagaMessage<FlightDTO> message = objectMapper.readValue(
                    json, new TypeReference<SagaMessage<FlightDTO>>() {}
                );
                message.setOrigin("CANCELAR_VOO");
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.falhou", jsonError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // LISTENER PARA COMPENSAÇÃO (REVERTER CANCELAMENTO)
    @RabbitListener(queues = "voo.cancelamento.compensar")
    public void onCompensateCancelFlight(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            
            FlightDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();

            System.out.println("[FLIGHT] Executando compensação de cancelamento para SAGA " + correlationId);
            System.out.println("[FLIGHT] Voltando voo " + dto.getCodigo_voo() + " para o status anterior");

            // Reverte o cancelamento - volta o voo para status CONFIRMADO
            flightService.revertFlightCancellation(dto.getCodigo_voo());
            
            System.out.println("[FLIGHT] Compensação de cancelamento concluída para voo " + dto.getCodigo_voo());
            
        } catch (Exception e) {
            System.err.println("[FLIGHT] Erro na compensação de cancelamento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
