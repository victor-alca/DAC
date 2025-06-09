package com.projetofuncionario.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.model.Employee;

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
  public void receiveRead(@Payload String json) {
    try {
      SagaMessage<Employee> message = objectMapper.readValue(json, new TypeReference<SagaMessage<Employee>>() {});

      Employee employee = message.getPayload();

      employeeService.save(employee);

      message.setOrigin("EMPLOYEE");
      rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.sucesso", message);

      System.out.println("[EMPLOYEE] Funcionario criado com sucesso: " + employee.getEmail());
    } catch (Exception e) {
      e.printStackTrace();
      try {
        // Em caso de erro, tenta enviar a mensagem de falha
        SagaMessage<Employee> failedMessage = new SagaMessage<>();
        failedMessage.setOrigin("EMPLOYEE");
        rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.falhou", failedMessage);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}