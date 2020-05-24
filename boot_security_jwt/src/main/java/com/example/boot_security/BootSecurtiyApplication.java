package com.example.boot_security;

import com.example.boot_security.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BootSecurtiyApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootSecurtiyApplication.class, args);
    }
}
