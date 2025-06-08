package backend.clients.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import backend.clients.message.SagaMessage;
import backend.clients.models.Client;

@Component
public class ConsumerInsertClient {
  @Autowired
  private ClientService clientService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "cliente.cadastro.iniciado.cliente")
  public void receiveRead(@Payload SagaMessage message) {
    try {
      Client client = message.getPayload();

      clientService.addClient(client);

      message.setOrigin("CLIENT");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso", message);

      System.out.println("[CLIENT] Cliente criado com sucesso: " + client.getEmail());
    } catch (Exception e) {
      e.printStackTrace();
      try {
        message.setOrigin("CLIENT");
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou", message);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}