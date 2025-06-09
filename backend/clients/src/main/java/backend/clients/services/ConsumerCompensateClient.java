package backend.clients.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            SagaMessage<Client> message = objectMapper.readValue(json, new TypeReference<SagaMessage<Client>>() {});
            Client client = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[AUTH] Executando rollback para SAGA " + correlationId);

                Client clientToDelete = clientRepository.findByCpf(client.getCpf());
                if (clientToDelete != null) {
                    clientRepository.delete(clientToDelete);
                    System.out.println("[CLIENT] Cliente com CPF " + client.getCpf() + " deletado com sucesso.");
                } else {
                    System.out.println("[CLIENT] Nenhum cliente encontrado com CPF " + client.getCpf());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
