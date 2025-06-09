package com.orchestrator.orchestrator.listener;

import java.util.Set;
import com.orchestrator.orchestrator.service.SagaUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.orchestrator.dto.ClientDTO;
import com.orchestrator.orchestrator.dto.EmployeeDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;

@Component
public class UserSagaListener {

    private final SagaUserService sagaUserService;
    @Autowired
    private ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;
    private final SagaStateManager sagaStateManager;

    public UserSagaListener(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager,
            SagaUserService sagaUserService) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaStateManager = sagaStateManager;
        this.sagaUserService = sagaUserService;
    }

    @RabbitListener(queues = "cliente.cadastro.sucesso")
    public void onUsuarioCriado(@Payload SagaMessage<ClientDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out
                    .println("[ORQUESTRADOR] Serviço " + origin + " confirmou sucesso (correlationId: " + correlationId
                            + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORQUESTRADOR] SAGA CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    sagaUserService.compensateSuccessfulServices(correlationId, message.getPayload());
                } else {
                    System.out.println(
                            "[ORQUESTRADOR] SAGA CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "cliente.cadastro.falhou")
    public void onUsuarioErro(@Payload SagaMessage<ClientDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " FALHOU (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);

            if (sagaStateManager.isComplete(correlationId)) {
                System.out.println("[ORQUESTRADOR] SAGA CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                sagaUserService.compensateSuccessfulServices(correlationId, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "funcionario.cadastro.sucesso")
    public void onUsuarioCriadoEmployee(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out
                    .println("[ORQUESTRADOR] Serviço " + origin + " confirmou sucesso (correlationId: " + correlationId
                            + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORQUESTRADOR] SAGA CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    sagaUserService.compensateSuccessfulServices(correlationId, message.getPayload());
                } else {
                    System.out.println(
                            "[ORQUESTRADOR] SAGA CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "funcionario.cadastro.falhou")
    public void onUsuarioErroEmployee(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " FALHOU (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);

            if (sagaStateManager.isComplete(correlationId)) {
                System.out.println("[ORQUESTRADOR] SAGA CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                sagaUserService.compensateSuccessfulServices(correlationId, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}