package com.projetofuncionario.service;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.dto.EmployeeDTO;
import com.projetofuncionario.message.SagaMessage;
import com.projetofuncionario.model.Employee;

@Component
public class ConsumerUpdateEmployee {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.editar.iniciado.funcionario")
    public void receiveRead(@Payload SagaMessage<EmployeeDTO> message) {
        try {
            EmployeeDTO employeeDTO = objectMapper.convertValue(message.getPayload(), EmployeeDTO.class);

            Optional<Employee> funcionario = employeeService.findByCpf(employeeDTO.cpf);

            if (funcionario.isPresent()) {
                Employee e = funcionario.get();
                e.setEmail(employeeDTO.email);
                e.setPhone(employeeDTO.phone);
                e.setName(employeeDTO.name);
                e.setActive(false);
                employeeService.save(e);
                System.out.println("[FUNCIONARIO] Funcionário editado com sucesso: " + employeeDTO.cpf);
            } else {
                System.err.println("[FUNCIONARIO] Funcionário não encontrado: " + employeeDTO.cpf);
            }

            message.setOrigin("EMPLOYEE");
            rabbitTemplate.convertAndSend("saga.exchange", "funcionario.editar.sucesso", message);

        } catch (ResponseStatusException e) {
            e.printStackTrace();
            try {
                SagaMessage<EmployeeDTO> failedMessage = new SagaMessage<>();
                failedMessage.setOrigin("EMPLOYEE");
                failedMessage.setCorrelationId(message.getCorrelationId());
                rabbitTemplate.convertAndSend("saga.exchange", "funcionario.editar.falhou", failedMessage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}