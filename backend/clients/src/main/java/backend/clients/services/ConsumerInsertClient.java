package backend.clients.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.clients.message.SagaMessage;
import backend.clients.models.Client;

@Component
public class ConsumerInsertClient {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ClientService clientService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "cliente.cadastro.iniciado.cliente")
  public void receiveRead(@Payload String json) {
    try {
      SagaMessage<Client> message = objectMapper.readValue(json, new TypeReference<SagaMessage<Client>>() {});
      Client client = message.getPayload();

      clientService.addClient(client);

      message.setOrigin("CLIENT");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso",
          objectMapper.writeValueAsString(message));

      System.out.println("[CLIENT] Cliente criado com sucesso: " + client.getEmail());
    } catch (Exception e) {
      e.printStackTrace();

      try {
        SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
        message.setOrigin("CLIENT");
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou",
            objectMapper.writeValueAsString(message));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}