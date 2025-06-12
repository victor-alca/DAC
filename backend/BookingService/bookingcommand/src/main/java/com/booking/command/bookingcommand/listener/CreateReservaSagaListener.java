package com.booking.command.bookingcommand.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.booking.command.bookingcommand.dtos.ReservationDTO;
import com.booking.command.bookingcommand.message.SagaMessage;
import com.booking.command.bookingcommand.service.BookingCommandService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CreateReservaSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookingCommandService bookingCommandService;

    @RabbitListener(queues = "reserva.criacao.iniciada.reserva")
    public void onReservaSaga(@Payload SagaMessage<ReservationDTO> message) {
        try {
            ReservationDTO dto = message.getPayload();

            // Cria a reserva e obtém o código
            String codigoReserva = bookingCommandService.createBookingBySaga(dto);

            // Adiciona o código da reserva no DTO de resposta
            dto.setCodigo_reserva(codigoReserva);
            message.setPayload(dto);
            message.setOrigin("RESERVA");

            String json_response = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.sucesso", json_response);
        } catch (Exception e) {
            if (message != null) {
                message.setOrigin("RESERVA");
            }
            String json_error;
            try {
                json_error = objectMapper.writeValueAsString(message);
            } catch (Exception ex) {
                json_error = "{}";
            }
            rabbitTemplate.convertAndSend("reserva.saga.exchange", "reserva.criacao.falhou", json_error);
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "reserva.reserva.compensar")
    public void onCompensate(@Payload SagaMessage<ReservationDTO> message) {
        try {
            ReservationDTO dto = message.getPayload();
            // Rollback: cancela a reserva criada
            bookingCommandService.cancelBookingBySaga(dto);
            System.out.println("[RESERVA] Rollback de reserva realizado para cliente " + dto.getCodigo_cliente());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}