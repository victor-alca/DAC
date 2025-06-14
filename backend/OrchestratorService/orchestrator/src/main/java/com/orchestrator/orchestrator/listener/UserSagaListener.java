package com.orchestrator.orchestrator.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.orchestrator.dto.ClientDTO;
import com.orchestrator.orchestrator.dto.EmployeeDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;
import com.orchestrator.orchestrator.service.SagaUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserSagaListener {

    @Autowired
    private ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;
    private final SagaStateManager sagaStateManager;
    private final SagaUserService sagaUserService;

    public UserSagaListener(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager,
                             SagaUserService sagaUserService) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaStateManager = sagaStateManager;
        this.sagaUserService = sagaUserService;
    }

    // ============================== CLIENTE ==============================

    @RabbitListener(queues = "cliente.cadastro.sucesso")
    public void onClientCriado(@Payload SagaMessage<ClientDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " confirmou sucesso (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORQUESTRADOR] SAGA CLIENTE CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    sagaUserService.compensateSuccessfulServicesClient(correlationId, message.getPayload());
                } else {
                    System.out.println("[ORQUESTRADOR] SAGA CLIENTE CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "cliente.cadastro.falhou")
    public void onClientErro(@Payload SagaMessage<ClientDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " FALHOU (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);

            if (message.getErrorInfo() != null) {
                sagaStateManager.setErrorInfo(correlationId, message.getErrorInfo());
            }

            if (sagaStateManager.isComplete(correlationId)) {
                System.out.println("[ORQUESTRADOR] SAGA CLIENTE CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                sagaUserService.compensateSuccessfulServicesClient(correlationId, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================== FUNCIONÁRIO ==============================

    @RabbitListener(queues = "funcionario.cadastro.sucesso")
    public void onFuncionarioCriado(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " confirmou sucesso (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORQUESTRADOR] SAGA FUNCIONÁRIO CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    sagaUserService.compensateSuccessfulServicesEmployee(correlationId, message.getPayload());
                } else {
                    System.out.println("[ORQUESTRADOR] SAGA FUNCIONÁRIO CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "funcionario.cadastro.falhou")
    public void onFuncionarioErro(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " FALHOU (correlationId: " + correlationId + ")");

            sagaStateManager.get(correlationId).markFailure(origin);

            if (message.getErrorInfo() != null) {
                sagaStateManager.setErrorInfo(correlationId, message.getErrorInfo());
            }

            if (sagaStateManager.isComplete(correlationId)) {
                System.out.println("[ORQUESTRADOR] SAGA FUNCIONÁRIO CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                sagaUserService.compensateSuccessfulServicesEmployee(correlationId, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "funcionario.excluir.sucesso")
    public void onFuncionarioExclusaoSucesso(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            String correlationId = message.getCorrelationId();
            String origin = message.getOrigin();

            System.out.println("[ORQUESTRADOR] Serviço " + origin + " confirmou exclusão com sucesso (correlationId: " + correlationId + ")");

            sagaStateManager.markSuccess(correlationId, origin);

            if (sagaStateManager.isComplete(correlationId)) {
                if (sagaStateManager.get(correlationId).hasFailure()) {
                    System.out.println("[ORQUESTRADOR] SAGA DE EXCLUSÃO FUNCIONÁRIO CONCLUÍDA COM ERRO (correlationId: " + correlationId + ")");
                    sagaUserService.compensateSuccessfulServicesEmployee(correlationId, message.getPayload());
                } else {
                    System.out.println("[ORQUESTRADOR] SAGA DE EXCLUSÃO FUNCIONÁRIO CONCLUÍDA COM SUCESSO (correlationId: " + correlationId + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
