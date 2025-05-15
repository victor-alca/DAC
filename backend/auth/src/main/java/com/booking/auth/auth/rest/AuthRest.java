package com.booking.auth.auth.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.auth.auth.DTO.AuthDTO;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("login")
public class AuthRest {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    ResponseEntity<User> login(@RequestBody AuthDTO login) {
        if (!userRepository.existsByEmail(login.getEmail())) {
            return ResponseEntity.status(401).body(new User());
        }

        User user = userRepository.findByEmail(login.getEmail());

        if (login.getPassword().equals(user.getPassword())) {
            return ResponseEntity.ok().body(user);
        }

        return ResponseEntity.status(401).body(new User());
    }

}
