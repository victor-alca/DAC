package com.booking.auth.auth.service;

import java.util.Map;
import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.booking.auth.auth.DTO.EmployeeDTO;
import com.booking.auth.auth.DTO.UserDTO;
import com.booking.auth.auth.email.EmailService;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.booking.auth.auth.utils.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerInsertUser {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @RabbitListener(queues = "cliente.cadastro.iniciado.auth")
  public void receiveReadClient(@Payload SagaMessage<Object> message) {
    try {
      // Converte o payload para UserDTO
      UserDTO client;
      Object payload = message.getPayload();
      
      if (payload instanceof UserDTO) {
        client = (UserDTO) payload;
      } else {
        client = objectMapper.convertValue(payload, UserDTO.class);
      }

      String generatedPassword = String.format("%04d", new Random().nextInt(10000));
      System.out.println("[AUTH] Generated password for user " + client.email + ": " + generatedPassword);

      String salt = HashUtil.generateSalt();
      String hashedPassword = HashUtil.hashPassword(generatedPassword, salt);

      User user = new User();
      user.setType("CLIENTE");
      user.setEmail(client.email);
      user.setPassword(hashedPassword);
      user.setSalt(salt);

      if (userRepository.findByEmail(user.getEmail()) != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "O Cliente já existe!");
      }

      userRepository.save(user);
      emailService.sendPasswordEmail(user.getEmail(), generatedPassword);

      message.setOrigin("AUTH");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso", message);

      System.out.println("[AUTH] Usuário CLIENTE criado com sucesso: " + user.getEmail());
    } catch (Exception e) {
      System.err.println("[AUTH ERROR] Erro ao processar cliente: " + e.getMessage());
      e.printStackTrace();
      try {
        message.setOrigin("AUTH");
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou", message);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  @RabbitListener(queues = "funcionario.cadastro.iniciado.auth")
  public void receiveReadEmployee(@Payload SagaMessage<Object> message) {
    try {
      System.out.println("[AUTH] Processando criação de funcionário...");

      // Converte o payload para EmployeeDTO
      EmployeeDTO employeeDTO;
      Object payload = message.getPayload();
      
      if (payload instanceof EmployeeDTO) {
        employeeDTO = (EmployeeDTO) payload;
      } else {
        employeeDTO = objectMapper.convertValue(payload, EmployeeDTO.class);
      }

      System.out.println("[AUTH DEBUG] EmployeeDTO: " + employeeDTO);

      String email = employeeDTO.getEmail();
      String senhaFornecida = employeeDTO.getSenha();

      System.out.println("[AUTH DEBUG] Email: " + email);
      System.out.println("[AUTH DEBUG] Senha fornecida: '" + senhaFornecida + "'");

      String finalPassword;
      if (senhaFornecida != null && !senhaFornecida.trim().isEmpty()) {
        finalPassword = senhaFornecida;
        System.out.println("[AUTH] USANDO SENHA FORNECIDA para " + email + ": " + finalPassword);
      } else {
        finalPassword = String.format("%04d", new Random().nextInt(10000));
        System.out.println("[AUTH] GERANDO SENHA AUTOMÁTICA para " + email + ": " + finalPassword);
      }

      String salt = HashUtil.generateSalt();
      String hashedPassword = HashUtil.hashPassword(finalPassword, salt);

      User user = new User();
      user.setType("FUNCIONARIO");
      user.setEmail(email);
      user.setPassword(hashedPassword);
      user.setSalt(salt);

      if (userRepository.findByEmail(user.getEmail()) != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "O Funcionario já existe!");
      }

      userRepository.save(user);

      // Só envia email se a senha foi gerada automaticamente
      if (senhaFornecida == null || senhaFornecida.trim().isEmpty()) {
        emailService.sendPasswordEmail(user.getEmail(), finalPassword);
        System.out.println("[AUTH] Email enviado com senha gerada");
      } else {
        System.out.println("[AUTH] Email NÃO enviado - senha foi fornecida pelo usuário");
      }

      message.setOrigin("AUTH");
      rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.sucesso", message);

      System.out.println("[AUTH] Usuário FUNCIONÁRIO criado com sucesso: " + user.getEmail());

    } catch (Exception e) {
      System.err.println("[AUTH ERROR] Erro ao processar funcionário: " + e.getMessage());
      e.printStackTrace();
      try {
        message.setOrigin("AUTH");
        rabbitTemplate.convertAndSend("saga.exchange", "funcionario.cadastro.falhou", message);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
