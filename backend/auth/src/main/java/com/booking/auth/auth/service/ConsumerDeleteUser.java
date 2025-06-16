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
public class ConsumerDeleteUser {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @RabbitListener(queues = "funcionario.excluir.auth")
  public void handleFuncionarioExclusao(@Payload SagaMessage<Object> message) {
    try {
      // Converte payload para EmployeeDTO
      EmployeeDTO employeeDTO;
      Object payload = message.getPayload();
      
      if (payload instanceof EmployeeDTO) {
        employeeDTO = (EmployeeDTO) payload;
      } else {
        employeeDTO = objectMapper.convertValue(payload, EmployeeDTO.class);
      }

      String email = employeeDTO.getEmail();

      System.out.println("[AUTH] Recebida requisição de exclusão para o e-mail: " + email);

      User user = userRepository.findByEmail(email);

      if (user != null) {
        userRepository.delete(user);
        System.out.println("[AUTH] Usuário removido com sucesso: " + email);
      } else {
        System.err.println("[AUTH] Usuário não encontrado: " + email);
      }

      message.setOrigin("AUTH");
      rabbitTemplate.convertAndSend("saga.exchange", "funcionario.excluido.sucesso", message);
    } catch (Exception e) {
      System.err.println("[AUTH] Erro ao processar exclusão: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
