package com.ville.intelligente.gestionincidents.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestPasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    public TestPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            String rawPassword = "MotDePasse123"; // remplace par ton mot de passe
            String encoded = passwordEncoder.encode(rawPassword);
            System.out.println("Mot de passe encodé BCrypt : " + encoded);
        };
    }
}
