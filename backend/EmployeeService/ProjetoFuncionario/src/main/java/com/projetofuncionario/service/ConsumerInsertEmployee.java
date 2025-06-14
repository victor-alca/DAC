package com.projetofuncionario.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.model.Employee;
import com.projetofuncionario.dto.EmployeeDTO;
import com.projetofuncionario.message.SagaMessage;

@Component
public class ConsumerInsertEmployee {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "funcionario.cadastro.iniciado.funcionario")
    public void receiveRead(@Payload SagaMessage message) {
        try {
            // Converte o payload para Map para ter flexibilidade
            @SuppressWarnings("unchecked")
            Map<String, Object> employeeData = (Map<String, Object>) message.getPayload();

            Employee employee = new Employee();
            employee.setCpf((String) employeeData.get("cpf"));
            employee.setEmail((String) employeeData.get("email"));
            employee.setActive(true);
            
            // CORREÇÃO: mapear os campos com nomes corretos
            String nome = (String) employeeData.get("nome");
            if (nome == null) {
                nome = (String) employeeData.get("name"); // fallback
            }
            employee.setName(nome);
            
            String telefone = (String) employeeData.get("telefone");
            if (telefone == null) {
                telefone = (String) employeeData.get("phone"); // fallback
            }
            employee.setPhone(telefone);

            employeeService.save(employee);

            message.setOrigin("EMPLOYEE");
            rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.sucesso", message);

            System.out.println("[EMPLOYEE] Funcionario criado com sucesso: " + employee.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // Em caso de erro, tenta enviar a mensagem de falha
                SagaMessage failedMessage = new SagaMessage<>();
                failedMessage.setPayload(message.getPayload());
                failedMessage.setOrigin("EMPLOYEE");
                failedMessage.setCorrelationId(message.getCorrelationId());
                rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.falhou", failedMessage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}