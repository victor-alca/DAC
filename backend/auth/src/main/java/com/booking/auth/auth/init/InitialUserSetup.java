package com.booking.auth.auth.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.booking.auth.auth.model.User;
import com.booking.auth.auth.repository.UserRepository;
import com.booking.auth.auth.utils.HashUtil;

@Configuration
public class InitialUserSetup {
    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("func_pre@gmail.com") == null) {
                String rawPassword = "TADS"; // Plain password for the admin
                String salt = HashUtil.generateSalt();
                String hashedPassword = HashUtil.hashPassword(rawPassword, salt);

                User employee = new User();
                employee.setType("FUNCIONARIO");
                employee.setEmail("func_pre@gmail.com");
                employee.setPassword(hashedPassword);
                employee.setSalt(salt);

                userRepository.save(employee);
                System.out.println("[INIT] Admin user created: admin@booking.com with password: " + rawPassword);
            }
        };
    }
}
