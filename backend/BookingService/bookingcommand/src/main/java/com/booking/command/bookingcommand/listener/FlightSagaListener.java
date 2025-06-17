package com.booking.command.bookingcommand.listener;

import com.booking.command.bookingcommand.dtos.FlightDTO;
import com.booking.command.bookingcommand.message.SagaMessage;
import com.booking.command.bookingcommand.service.BookingCommandService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class FlightSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingCommandService bookingCommandService;

    @RabbitListener(queues = "voo.cancelamento.reservas.iniciado.reserva")
    public void onCancelFlightReservationsSaga(@Payload SagaMessage<FlightDTO> message) {
        try {
            FlightDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();
            
            System.out.println("[CANCELAR_RESERVAS_VOO] Processando cancelamento de reservas do voo " + dto.getCodigo_voo());
            
            // Cancela todas as reservas do voo
            boolean success = bookingCommandService.cancelBookingsByFlight(dto.getCodigo_voo());
            message.setOrigin("CANCELAR_RESERVAS_VOO");

            if (success) {
                String jsonResponse = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.reservas.sucesso", jsonResponse);
                System.out.println("[CANCELAR_RESERVAS_VOO] Reservas do voo " + dto.getCodigo_voo() + " canceladas com sucesso na SAGA");
            } else {
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.reservas.falhou", jsonError);
                System.out.println("[CANCELAR_RESERVAS_VOO] Falha ao cancelar reservas do voo " + dto.getCodigo_voo() + " na SAGA");
            }
        } catch (Exception e) {
            System.err.println("[CANCELAR_RESERVAS_VOO] Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            try {
                message.setOrigin("CANCELAR_RESERVAS_VOO");
                String jsonError = objectMapper.writeValueAsString(message);
                rabbitTemplate.convertAndSend("voo.saga.exchange", "voo.cancelamento.reservas.falhou", jsonError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // LISTENER PARA COMPENSAÇÃO (REVERTER CANCELAMENTO DAS RESERVAS)
    @RabbitListener(queues = "voo.reservas.cancelamento.compensar")
    public void onCompensateCancelFlightReservations(@Payload String json) {
        try {
            SagaMessage<FlightDTO> message = objectMapper.readValue(
                json, new TypeReference<SagaMessage<FlightDTO>>() {}
            );
            
            FlightDTO dto = message.getPayload();
            String correlationId = message.getCorrelationId();

            System.out.println("[BOOKING] Executando compensação de cancelamento de reservas para SAGA " + correlationId);
            System.out.println("[BOOKING] Voltando reservas do voo " + dto.getCodigo_voo() + " para o status anterior");

            // Reverte o cancelamento das reservas
            bookingCommandService.revertFlightReservationsCancellation(dto.getCodigo_voo());
            
            System.out.println("[BOOKING] Compensação de cancelamento de reservas concluída para voo " + dto.getCodigo_voo());
            
        } catch (Exception e) {
            System.err.println("[BOOKING] Erro na compensação de cancelamento de reservas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}