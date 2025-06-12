package backend.clients.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.clients.dto.ClientDTO;
import backend.clients.message.SagaMessage;
import backend.clients.models.Client;
import backend.clients.repository.ClientRepository;

@Component
public class ConsumerCompensateClient {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @RabbitListener(queues = "cliente.client.compensar")
    public void receiveRead(@Payload String json) {
        try {
            SagaMessage<ClientDTO> message = objectMapper.readValue(json, new TypeReference<SagaMessage<ClientDTO>>() {});
            ClientDTO client = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[AUTH] Executando rollback para SAGA " + correlationId);

                Client clientToDelete = clientRepository.findByCpf(client.cpf);
                if (clientToDelete != null) {
                    clientRepository.delete(clientToDelete);
                    System.out.println("[CLIENT] Cliente com CPF " + client.cpf + " deletado com sucesso.");
                } else {
                    System.out.println("[CLIENT] Nenhum cliente encontrado com CPF " + client.cpf);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
