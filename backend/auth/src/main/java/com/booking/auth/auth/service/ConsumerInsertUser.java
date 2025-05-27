package com.booking.auth.auth.service;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.email.EmailService;
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

    @RabbitListener(queues = "create-auth-user")
    public void receiveRead(@Payload String json) {
        System.out.println(json);
        try {
            User user = objectMapper.readValue(json, User.class);
            String generatedPassword = String.format("%04d", new Random().nextInt(10000));
            System.out.println("Generated password for user " + user.getEmail() + ": " + generatedPassword);

            String salt = HashUtil.generateSalt();
            String hashedPassword = HashUtil.hashPassword(generatedPassword, salt);

            user.setPassword(hashedPassword);
            user.setSalt(salt);

            userRepository.save(user);
            emailService.sendPasswordEmail(user.getEmail(), generatedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
