package com.booking.auth.auth.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerInsertUser {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "create-auth-user")
    public void receiveRead(@Payload String json) {
        try {
            User user = objectMapper.readValue(json, User.class);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
