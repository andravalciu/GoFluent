package com.gofluent.backend.config;

import com.gofluent.backend.entities.Role;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdminIfNotExists() {
        return args -> {
            String adminLogin = "admin";

            if (userRepository.findByLogin(adminLogin).isEmpty()) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .login(adminLogin)
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);
                System.out.println("✅ Admin user created: login='admin', password='admin123'");
            } else {
                System.out.println("ℹ️ Admin user already exists.");
            }
        };
    }
}

