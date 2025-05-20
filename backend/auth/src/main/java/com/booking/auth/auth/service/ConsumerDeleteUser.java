package com.booking.auth.auth.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerDeleteUser {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "delete-auth-user")
    public void receiveRead(@Payload String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String id = jsonNode.get("id").asText();

            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                System.out.println("User deleted with ID: " + id);
            } else {
                System.err.println("User not found with ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
