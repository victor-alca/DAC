
package com.orchestrator.orchestrator.service;

import java.util.Set;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.orchestrator.orchestrator.dto.ClientDTO;
import com.orchestrator.orchestrator.message.SagaMessage;
import com.orchestrator.orchestrator.saga.SagaStateManager;

@Service
public class SagaUserService {
  private final RabbitTemplate rabbitTemplate;
  private final SagaStateManager sagaStateManager;

  public SagaUserService(RabbitTemplate rabbitTemplate, SagaStateManager sagaStateManager) {
    this.rabbitTemplate = rabbitTemplate;
    this.sagaStateManager = sagaStateManager;
  }

  public String startUserRegistrationSaga(ClientDTO clientDTO) {
    SagaMessage<ClientDTO> sagaMessage = new SagaMessage<ClientDTO>(clientDTO);
    String correlationId = sagaMessage.getCorrelationId();

    sagaStateManager.createSaga(correlationId, Set.of("CLIENT", "AUTH"));
    rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.iniciado", sagaMessage);

    System.out.println("[SAGA] Iniciando cadastro de cliente com correlationId: " + sagaMessage.getCorrelationId());

    return correlationId;
  }

  public void compensateSuccessfulServices(String correlationId, ClientDTO payload) {
    Set<String> servicosComSucesso = sagaStateManager.get(correlationId).getSuccessfulServices();

    for (String service : servicosComSucesso) {
      SagaMessage<ClientDTO> compensacaoMessage = new SagaMessage<>(payload);
      compensacaoMessage.setCorrelationId(correlationId);
      compensacaoMessage.setOrigin("ORCHESTRATOR");
      compensacaoMessage.setOperation("DELETE");

      String routingKey = "cliente." + service.toLowerCase() + ".compensar";
      rabbitTemplate.convertAndSend("saga.exchange", routingKey, compensacaoMessage);

      System.out.println("[ORQUESTRADOR] Enviando COMPENSATE para serviço " + service);
    }
  }

}