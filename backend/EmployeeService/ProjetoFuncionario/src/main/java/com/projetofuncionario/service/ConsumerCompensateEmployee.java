package com.projetofuncionario.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.message.SagaMessage;
import com.projetofuncionario.model.Employee;

@Component
public class ConsumerCompensateEmployee {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "funcionario.employee.compensar")
  public void compensateEmployee(@Payload SagaMessage message) { // MUDANÇA: Recebe SagaMessage em vez de Map
    try {
      String correlationId = message.getCorrelationId();

      // Extrai o email do payload
      Object payload = message.getPayload();
      Map<String, Object> employeeData;

      if (payload instanceof Map) {
        employeeData = (Map<String, Object>) payload;
      } else {
        employeeData = objectMapper.convertValue(payload, Map.class);
      }

      String email = (String) employeeData.get("email");

      if (email != null) {
        Optional<Employee> employee = employeeService.findByEmail(email);
        if (employee.isPresent()) {
          employeeService.delete(employee.get().getId());
          System.out.println("[EMPLOYEE] Funcionário compensado (removido): " + email);
        }
      }

      // Confirma a compensação usando SagaMessage
      SagaMessage compensationResponse = new SagaMessage<>();
      compensationResponse.setCorrelationId(correlationId);
      compensationResponse.setOrigin("EMPLOYEE");

      rabbitTemplate.convertAndSend("saga.exchange", "funcionario.employee.compensado", compensationResponse);

    } catch (Exception e) {
      System.err.println("[EMPLOYEE] Erro na compensação: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
