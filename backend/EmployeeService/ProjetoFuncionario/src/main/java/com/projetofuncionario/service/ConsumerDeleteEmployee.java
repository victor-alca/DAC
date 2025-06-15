package com.projetofuncionario.service;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetofuncionario.dto.EmployeeDTO;
import com.projetofuncionario.message.SagaMessage;
import com.projetofuncionario.model.Employee;

@Component
public class ConsumerDeleteEmployee {
  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @RabbitListener(queues = "funcionario.excluir.funcionario")
  public void handleFuncionarioInativacao(@Payload SagaMessage<EmployeeDTO> message) {
    EmployeeDTO employeeRaw = objectMapper.convertValue(message.getPayload(), EmployeeDTO.class);
    String cpf = employeeRaw.cpf;
    Optional<Employee> funcionario = employeeService.findByCpf(cpf);

    if (funcionario.isPresent()) {
      Employee e = funcionario.get();
      e.setActive(false);
      employeeService.save(e);
      System.out.println("[FUNCIONARIO] Funcionário inativado com sucesso: " + cpf);
    } else {
      System.err.println("[FUNCIONARIO] Funcionário não encontrado: " + cpf);
    }

    message.setOrigin("EMPLOYEE");
    rabbitTemplate.convertAndSend("saga.exchange", "funcionario.excluido.sucesso", message);
  }
}
