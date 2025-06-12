package com.booking.auth.auth.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.booking.auth.auth.DTO.AuthDTO;
import com.booking.auth.auth.DTO.LoginResponseDTO;
import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.booking.auth.auth.utils.HashUtil;
import com.booking.auth.auth.utils.JwtUtil;

@CrossOrigin
@RestController
public class AuthRest {
  @Autowired
  private UserRepository userRepository;

  @PostMapping("login")
  ResponseEntity<?> login(@RequestBody AuthDTO login) {
    System.out.println(login);
    if (!userRepository.existsByEmail(login.getLogin())) {
      return ResponseEntity.status(401).body("Invalid credentials");
    }

    User user = userRepository.findByEmail(login.getLogin());

    try {
      String hashedInputPassword = HashUtil.hashPassword(login.getSenha(), user.getSalt());

      if (hashedInputPassword.equals(user.getPassword())) {
        String token = JwtUtil.generateToken(user.getEmail(), user.getType());

        LoginResponseDTO response = new LoginResponseDTO(token, user.getType(), user);
        return ResponseEntity.ok(response);

      } else {
        return ResponseEntity.status(401).body("Invalid credentials");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Server error");
    }
  }

}
