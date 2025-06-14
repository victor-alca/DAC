package com.booking.auth.auth.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.DTO.ClientDTO;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerCompensateUser {

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "cliente.auth.compensar")
    public void receiveRead(@Payload SagaMessage message) {
        try {
            ClientDTO client = message.getPayload();
            String operation = message.getOperation();

            if ("DELETE".equalsIgnoreCase(operation)) {
                String correlationId = message.getCorrelationId();
                System.out.println("[AUTH] Executando rollback para SAGA " + correlationId);

                User user = userRepository.findByEmail(client.email);
                if (user != null) {
                    userRepository.deleteById(user.getId());
                    System.out.println("[AUTH] Usuário com email " + user.getEmail() + " removido.");
                } else {
                    System.out.println("[AUTH] Usuário com email " + client.email + " não encontrado.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
