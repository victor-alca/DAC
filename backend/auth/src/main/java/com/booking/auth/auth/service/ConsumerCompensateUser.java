package com.booking.auth.auth.service;

import com.booking.auth.auth.DTO.UserDTO;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerCompensateUser {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    // ============================== CLIENTE ==============================

    @RabbitListener(queues = "cliente.auth.compensar")
    public void receiveReadClient(@Payload String json) {
        try {
            SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
            UserDTO client = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[AUTH] Executando rollback CLIENTE para SAGA " + correlationId);

                User user = userRepository.findByEmail(client.email);
                if (user != null) {
                    userRepository.deleteById(user.getId());
                    System.out.println("[AUTH] Usuário CLIENTE com email " + user.getEmail() + " removido.");
                } else {
                    System.out.println("[AUTH] Usuário CLIENTE com email " + client.email + " não encontrado.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================== FUNCIONÁRIO ==============================

    @RabbitListener(queues = "funcionario.auth.compensar")
    public void receiveReadEmployee(@Payload String json) {
        try {
            SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
            UserDTO employee = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[AUTH] Executando rollback FUNCIONÁRIO para SAGA " + correlationId);

                User user = userRepository.findByEmail(employee.email);
                if (user != null) {
                    userRepository.deleteById(user.getId());
                    System.out.println("[AUTH] Usuário FUNCIONÁRIO com email " + user.getEmail() + " removido.");
                } else {
                    System.out.println("[AUTH] Usuário FUNCIONÁRIO com email " + employee.email + " não encontrado.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
