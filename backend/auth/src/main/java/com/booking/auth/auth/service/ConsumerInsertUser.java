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

@Component
public class ConsumerInsertUser {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EmailService emailService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "cliente.cadastro.iniciado.auth")
  public void receiveRead(@Payload SagaMessage message) {
    try {
      System.out.println(message.getPayload());
      ClientDTO client = message.getPayload();

      String generatedPassword = String.format("%04d", new Random().nextInt(10000));
      System.out.println("Generated password for user " + client.getEmail() + ": " + generatedPassword);

      String salt = HashUtil.generateSalt();
      String hashedPassword = HashUtil.hashPassword(generatedPassword, salt);

      User user = new User();
      user.setType("CLIENTE");
      user.setEmail(client.getEmail());
      user.setPassword(hashedPassword);
      user.setSalt(salt);
      System.out.println(user);
      
      userRepository.save(user);

      emailService.sendPasswordEmail(user.getEmail(), generatedPassword);

      message.setOrigin("AUTH");
      rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.sucesso", message);

      System.out.println("[AUTH] Usuário criado com sucesso: " + user.getEmail());
    } catch (Exception e) {
      e.printStackTrace();
      try {
        message.setOrigin("AUTH");
        rabbitTemplate.convertAndSend("saga.exchange", "cliente.cadastro.falhou", message);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
