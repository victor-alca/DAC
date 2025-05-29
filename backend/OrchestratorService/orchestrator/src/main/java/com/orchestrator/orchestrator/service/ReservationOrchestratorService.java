package com.orchestrator.orchestrator.service;

import com.orchestrator.orchestrator.config.ClientSagaConfig;
import com.orchestrator.orchestrator.config.FlightSagaConfig;
import com.orchestrator.orchestrator.config.ReservationSagaConfig;
import com.orchestrator.orchestrator.dto.SagaMessageDTO;
import com.orchestrator.orchestrator.enums.ReservationSagaStep;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservationOrchestratorService {

    private final RabbitTemplate rabbitTemplate;

    public ReservationOrchestratorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Inicia a saga de reserva enviando a primeira mensagem para o serviço de reservas.
     * Gera um sagaId único para rastreamento.
     */
    public void startSaga(String reservationId) {
        SagaMessageDTO message = new SagaMessageDTO();
        message.setSagaId(UUID.randomUUID().toString());
        message.setReservationId(reservationId);
        message.setStep(ReservationSagaStep.START_RESERVATION);
        message.setStatus("SUCCESS");

        // Publica mensagem inicial na fila do serviço de reservas
        rabbitTemplate.convertAndSend(
            ReservationSagaConfig.RESERVATION_EXCHANGE,
            ReservationSagaConfig.RESERVATION_ROUTING_KEY,
            message
        );
    }

    /**
     * Processa o passo atual da saga e encaminha para o próximo serviço.
     */
    public void processStep(SagaMessageDTO message) {
        switch (message.getStep()) {
            case RESERVATION_CREATED -> sendToFlightService(message); // Reserva criada, aciona serviço de voo
            case FLIGHT_UPDATED -> sendToClientService(message);      // Voo atualizado, aciona serviço de cliente
            case CLIENT_UPDATED -> completeSaga(message);             // Cliente atualizado, finaliza saga
            case FAILED -> rollback(message);                         // Alguma etapa falhou, inicia rollback
            default -> System.out.println("Passo desconhecido: " + message.getStep());
        }
    }

    /**
     * Encaminha a mensagem para o serviço de voos atualizar o status do voo.
     */
    private void sendToFlightService(SagaMessageDTO message) {
        message.setStep(ReservationSagaStep.FLIGHT_UPDATED);
        rabbitTemplate.convertAndSend(
            FlightSagaConfig.FLIGHT_EXCHANGE,
            FlightSagaConfig.FLIGHT_ROUTING_KEY,
            message
        );
    }

    /**
     * Encaminha a mensagem para o serviço de clientes atualizar o status do cliente.
     */
    private void sendToClientService(SagaMessageDTO message) {
        message.setStep(ReservationSagaStep.CLIENT_UPDATED);
        rabbitTemplate.convertAndSend(
            ClientSagaConfig.CLIENT_EXCHANGE,
            ClientSagaConfig.CLIENT_ROUTING_KEY,
            message
        );
    }

    /**
     * Finaliza a saga com sucesso.
     * Retorna mensagem de conclusão para o gateway.
     */
    private void completeSaga(SagaMessageDTO message) {
        message.setStep(ReservationSagaStep.COMPLETED);
        System.out.println("SAGA finalizada com sucesso para reserva: " + message.getReservationId());
    }

    /**
     * Realiza o rollback da saga conforme o ponto de falha.
     */
    private void rollback(SagaMessageDTO message) {
        // Se falhou no CLIENT_UPDATED, desfaz cliente, depois voo, depois reserva.
        // Se falhou no FLIGHT_UPDATED, desfaz voo, depois reserva........
        System.out.println("Iniciando rollback para reserva: " + message.getReservationId());
    }
}