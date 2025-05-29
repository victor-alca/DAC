package com.orchestrator.orchestrator.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.orchestrator.orchestrator.dto.SagaMessageDTO;
import com.orchestrator.orchestrator.enums.ReservationSagaStep;
import com.orchestrator.orchestrator.service.ReservationOrchestratorService;

@Component
public class ReservationSagaListener {

    private final ReservationOrchestratorService orchestratorService;

    public ReservationSagaListener(ReservationOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @RabbitListener(queues = "orchestrator.saga.queue")
    public void onMessage(@Payload SagaMessageDTO message) {
        if ("SUCCESS".equals(message.getStatus())) {
            orchestratorService.processStep(message);
        } else {
            message.setStep(ReservationSagaStep.FAILED);
            orchestratorService.processStep(message);
        }
    }
}
