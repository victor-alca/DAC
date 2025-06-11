package com.flight.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.dto.ReservationDTO;
import com.flight.message.SagaMessage;
import com.flight.service.FlightService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CreateReservaSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FlightService flightService;

    @RabbitListener(queues = "reserva.criacao.iniciada.voo")
    public void onReservaSaga(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
            ReservationDTO dto = message.getPayload();

            // Tenta reservar poltronas - pode lançar ResponseStatusException
            boolean ok = flightService.reservarPoltronas(dto.getCodigo_voo(), dto.getQuantidade_poltronas());
            message.setOrigin("VOO");

            if (ok) {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.sucesso", objectMapper.writeValueAsString(message));
            } else {
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            }
        } catch (ResponseStatusException e) {
            // Captura exceções específicas (CONFLICT, NOT_FOUND, etc.)
            System.err.println("[VOO] Falha na SAGA - " + e.getStatusCode() + ": " + e.getReason());
            
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
                message.setOrigin("VOO");
                
                // Adiciona informações do erro na mensagem
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("errorCode", e.getStatusCode().value());
                errorInfo.put("errorMessage", e.getReason());
                message.setErrorInfo(errorInfo);
                
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            // Outros erros inesperados
            e.printStackTrace();
            try {
                SagaMessage<ReservationDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ReservationDTO>>() {});
                message.setOrigin("VOO");
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", objectMapper.writeValueAsString(message));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = "reserva.voo.compensar")
    public void onCompensate(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            ReservationDTO dto = message.getPayload();
            // Rollback: libera as poltronas reservadas
            flightService.liberarPoltronas(dto.getCodigo_voo(), dto.getQuantidade_poltronas());
            System.out.println("[VOO] Rollback de poltronas realizado para voo " + dto.getCodigo_voo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
