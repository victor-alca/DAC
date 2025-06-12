package backend.clients.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.clients.dto.ClientDTO;
import backend.clients.message.SagaMessage;
@Component
public class ConsumerInsertClient {
  @Autowired
  private ClientService clientService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @RabbitListener(queues = "cliente.cadastro.iniciado.cliente")
  public void receiveRead(@Payload String json) throws JsonMappingException, JsonProcessingException {
    SagaMessage<ClientDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ClientDTO>>() {});
    try {
      ClientDTO client = message.getPayload();

      clientService.addClient(client);

      message.setOrigin("CLIENT");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso", message);

    } catch (Exception e) {
      e.printStackTrace();
      try {
        // Em caso de erro, tenta enviar a mensagem de falha
        SagaMessage<ClientDTO> failedMessage = new SagaMessage<>();
        failedMessage.setOrigin("CLIENT");
        failedMessage.setCorrelationId(message.getCorrelationId());
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou", failedMessage);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}