package com.booking.auth.auth.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.DTO.EmployeeDTO;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerUpdateUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.editar.iniciado.auth")
    public void handleFuncionarioEdicao(@Payload SagaMessage<Object> message) {
        try {
            // Converte payload para EmployeeDTO
            EmployeeDTO employeeDTO;
            Object payload = message.getPayload();

            if (payload instanceof EmployeeDTO) {
                employeeDTO = (EmployeeDTO) payload;
            } else {
                employeeDTO = objectMapper.convertValue(payload, EmployeeDTO.class);
            }

            User user = userRepository.findByCpf(employeeDTO.getCpf());

            System.out.println("[AUTH] Recebida requisição de edição para o cpf: " + user.getCpf());
            if (user != null) {
                user.setEmail(employeeDTO.getEmail());

                userRepository.save(user);

                System.out.println("[AUTH] Usuário editado com sucesso: " + user.getCpf());
            } else {
                System.err.println("[AUTH] Usuário não encontrado: " + user.getCpf());
            }

            message.setOrigin("AUTH");
            rabbitTemplate.convertAndSend("saga.exchange", "funcionario.editar.sucesso", message);
        } catch (Exception e) {
            System.err.println("[AUTH] Erro ao processar edição: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
