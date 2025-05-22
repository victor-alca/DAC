package com.booking.auth.auth.service;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConsumerUpdateUser {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "update-auth-user")
    public void receiveRead(@Payload String json) {
        try {
            User updatedUser = objectMapper.readValue(json, User.class);

            Optional<User> user = userRepository.findById(updatedUser.getId());
            if (user.isPresent()) {
                User existingUser = user.get();

                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setPassword(updatedUser.getPassword());
                existingUser.setType(updatedUser.getType());

                userRepository.save(existingUser);
                System.out.println("User updated: " + existingUser);
            } else {
                System.err.println("User not found with ID: " + updatedUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
