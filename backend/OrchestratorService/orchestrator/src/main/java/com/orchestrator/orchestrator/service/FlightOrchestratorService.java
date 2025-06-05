package com.orchestrator.orchestrator.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.orchestrator.orchestrator.config.ClientSagaConfig;
import com.orchestrator.orchestrator.config.FlightSagaConfig;
import com.orchestrator.orchestrator.config.ReservationSagaConfig;
import com.orchestrator.orchestrator.dto.CancelationSagaMessageDTO;
import com.orchestrator.orchestrator.dto.SagaMessageDTO;
import com.orchestrator.orchestrator.enums.CancelationSagaStep;
import com.orchestrator.orchestrator.enums.ReservationSagaStep;

@Service
public class FlightOrchestratorService {

    private final RabbitTemplate rabbitTemplate;

    public FlightOrchestratorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Inicia a saga de voo enviando a primeira mensagem para o serviço de voos.
     * Gera um sagaId único para rastreamento.
     * ainda precisa fazer uma saga para cancelar e outra para confirmar (RF13/RF14)
     */
    public void startSaga(String flightId) {
        CancelationSagaMessageDTO message = new CancelationSagaMessageDTO();
        message.setSagaId(UUID.randomUUID().toString());
        message.setFlightId(flightId);
        message.setStep(CancelationSagaStep.START_CANCELATION);
        message.setStatus("SUCCESS");

        // Publica mensagem inicial na fila do serviço de reservas
        rabbitTemplate.convertAndSend(
            FlightSagaConfig.FLIGHT_EXCHANGE,
            FlightSagaConfig.FLIGHT_ROUTING_KEY,
            message
        );
    }

    
    /**
     * Processa o passo atual da saga e encaminha para o próximo serviço.
     */
    public void processStep(CancelationSagaMessageDTO message) {
        switch (message.getStep()) {
            case FLIGHT_CANCELLED -> sendToBookingService(message); // Voo Cancelado, seguir para reserva==s
            case BOOKINGS_UPDATED -> completeSaga(null);      // reservas atualizadas, concluir
            case FAILED -> rollback(message);                         // Alguma etapa falhou, inicia rollback
            default -> System.out.println("Passo desconhecido: " + message.getStep());
        }
    }

    /**
     * Encaminha a mensagem para o serviço de voos atualizar o status do voo.
     */
    private void sendToBookingService(CancelationSagaMessageDTO message) {
        message.setStep(CancelationSagaStep.FLIGHT_CANCELLED);
        rabbitTemplate.convertAndSend(
            ReservationSagaConfig.RESERVATION_EXCHANGE,
            ReservationSagaConfig.RESERVATION_ROUTING_KEY,
            message
        );
    }

    /**
     * Finaliza a saga com sucesso.
     * Retorna mensagem de conclusão para o gateway.
     */
    private void completeSaga(CancelationSagaMessageDTO message) {
        message.setStep(CancelationSagaStep.COMPLETED);
        System.out.println("SAGA finalizada com sucesso para cancelamento do voo: " + message.getFlightId());
    }

    /**
     * Realiza o rollback da saga conforme o ponto de falha.
     */
    private void rollback(CancelationSagaMessageDTO message) {
        // Se falhou no BOOKINGS_UPDATED, desfaz reservas, depois voo.
        // Se falhou no FLIGHT_CANCELLED, desfaz voo.
        System.out.println("Iniciando rollback para cancelamento do voo: " + message.getFlightId());
    }
}
