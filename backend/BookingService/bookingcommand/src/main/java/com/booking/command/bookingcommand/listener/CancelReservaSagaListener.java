package com.booking.command.bookingcommand.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.booking.command.bookingcommand.dtos.ReservationDTO;
import com.booking.command.bookingcommand.message.SagaMessage;
import com.booking.command.bookingcommand.service.BookingCommandService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CancelReservaSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingCommandService bookingCommandService;

    // LISTENER PARA INICIAR O CANCELAMENTO (PRIMEIRO PASSO DA SAGA)
    @RabbitListener(queues = "reserva.cancelamento.iniciado.reserva")
    public void onCancelReservaSaga(@Payload SagaMessage<ReservationDTO> message) {
        try {
            ReservationDTO dto = message.getPayload();
            
            System.out.println("[CANCELAR_RESERVA] Recebido pedido de cancelamento para reserva: " + dto.getCodigo_reserva());
            
            // Aqui só temos o código da reserva, mas o service vai buscar e preencher todos os dados
            boolean ok = bookingCommandService.cancelBooking(dto);
            message.setOrigin("CANCELAR_RESERVA");
            message.setPayload(dto); // DTO agora foi atualizado com dados completos da reserva

            if (ok) {
                String jsonResponse = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.sucesso", jsonResponse);
                System.out.println("[CANCELAR_RESERVA] Reserva " + dto.getCodigo_reserva() + " cancelada com sucesso na SAGA");
            } else {
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.falhou", jsonError);
                System.out.println("[CANCELAR_RESERVA] Falha ao cancelar reserva " + dto.getCodigo_reserva() + " na SAGA");
            }
        } catch (ResponseStatusException e) {
            // Captura exceções específicas (CONFLICT, NOT_FOUND, etc.)
            System.err.println("[CANCELAR_RESERVA] Falha na SAGA - " + e.getStatusCode() + ": " + e.getReason());
            
            try {
                message.setOrigin("CANCELAR_RESERVA");
                
                // Adiciona informações do erro na mensagem
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("errorCode", e.getStatusCode().value());
                errorInfo.put("errorMessage", e.getReason());
                message.setErrorInfo(errorInfo);
                                
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.falhou", jsonError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            // Outros erros inesperados
            System.err.println("[CANCELAR_RESERVA] Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            try {
                message.setOrigin("CANCELAR_RESERVA");
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.cancelamento.falhou", jsonError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // LISTENER PARA COMPENSAÇÃO (REVERTER CANCELAMENTO)
    @RabbitListener(queues = "reserva.cancelar.compensar")
    public void onCompensateCancelReserva(@Payload String json) {
        try {
            SagaMessage<ReservationDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<ReservationDTO>>() {}
            );
            
            ReservationDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();

            System.out.println("[BOOKING] Executando compensação de cancelamento para SAGA " + correlationId);
            System.out.println("[BOOKING] Voltando reserva " + dto.getCodigo_reserva() + " para o status anterior");

            // Reverte o cancelamento - volta a reserva para status anterior
            bookingCommandService.revertCancellationBySaga(dto);

            System.out.println("[BOOKING] Compensação de cancelamento concluída para reserva " + dto.getCodigo_reserva());
            
        } catch (Exception e) {
            System.err.println("[BOOKING] Erro na compensação de cancelamento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}