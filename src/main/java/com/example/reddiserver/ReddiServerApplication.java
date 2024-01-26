package com.example.reddiserver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {@Server(url = "https://api.reddi.kr", description = "배포서버 URL"), @Server(url = "http://localhost:8080", description = "로컬서버 URL")})
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class ReddiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReddiServerApplication.class, args);
    }
}
