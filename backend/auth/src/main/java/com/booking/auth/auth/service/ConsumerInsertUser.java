package com.booking.auth.auth.service;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.DTO.ClientDTO;
import com.booking.auth.auth.email.EmailService;
import com.booking.auth.auth.message.SagaMessage;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.booking.auth.auth.utils.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerInsertUser {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "cliente.cadastro.iniciado.auth")
  public void receiveRead(@Payload String json) {
    try {
      SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
      ClientDTO client = message.getPayload();

      String generatedPassword = String.format("%04d", new Random().nextInt(10000));
      System.out.println("Generated password for user " + client.getEmail() + ": " + generatedPassword);

      String salt = HashUtil.generateSalt();
      String hashedPassword = HashUtil.hashPassword(generatedPassword, salt);

      User user = new User();
      user.setEmail(client.getEmail());
      user.setPassword(hashedPassword);
      user.setSalt(salt);
      user.setType("CLIENTE");

      userRepository.save(user);

      emailService.sendPasswordEmail(user.getEmail(), generatedPassword);

      message.setOrigin("AUTH");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso",
          objectMapper.writeValueAsString(message));

      System.out.println("[AUTH] UsuÃ¡rio criado com sucesso: " + user.getEmail());
    } catch (Exception e) {
      e.printStackTrace();

      try {
        SagaMessage message = objectMapper.readValue(json, SagaMessage.class);
        message.setOrigin("AUTH");
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou",
            objectMapper.writeValueAsString(message));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
