package com.booking.auth.auth.service;

import com.booking.auth.auth.DTO.EmployeeDTO;
import com.booking.auth.auth.DTO.UserDTO;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerCompensateUser {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // ============================== CLIENTE ==============================

    @RabbitListener(queues = "cliente.auth.compensar")
    public void receiveReadClient(@Payload SagaMessage<Object> message) {
        try {
            // Converte payload para UserDTO
            UserDTO client;
            Object payload = message.getPayload();
            
            if (payload instanceof UserDTO) {
                client = (UserDTO) payload;
            } else {
                client = objectMapper.convertValue(payload, UserDTO.class);
            }

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
            System.err.println("[AUTH] Erro na compensação do cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================== FUNCIONÁRIO ==============================

    @RabbitListener(queues = "funcionario.auth.compensar")
    public void compensateEmployee(@Payload SagaMessage<Object> message) {
        try {
            String correlationId = message.getCorrelationId();

            // Converte payload para EmployeeDTO
            EmployeeDTO employeeDTO;
            Object payload = message.getPayload();
            
            if (payload instanceof EmployeeDTO) {
                employeeDTO = (EmployeeDTO) payload;
            } else {
                employeeDTO = objectMapper.convertValue(payload, EmployeeDTO.class);
            }

            String email = employeeDTO.getEmail();

            if (email != null) {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    userRepository.delete(user);
                    System.out.println("[AUTH] Usuário compensado (removido): " + email);
                }
            }

            // Confirma a compensação
            rabbitTemplate.convertAndSend("saga.exchange", "funcionario.auth.compensado", 
                Map.of("correlationId", correlationId, "status", "COMPENSATED"));

        } catch (Exception e) {
            System.err.println("[AUTH] Erro na compensação do funcionário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}