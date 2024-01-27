package com.example.reddiserver.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;


@Configuration
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_NAME = "authorization";	// 추가

//    @Bean
//    @Profile("local")
//    public OpenAPI localOpenAPIBuilder() {
//        return new OpenAPI().addServersItem(new Server().url("http://localhost:8080"));
//    }
//
//    @Bean
//    @Profile("dev")
//    public OpenAPI devOpenAPIBuilder() {
//        return new OpenAPI().addServersItem(new Server().url("https://api.reddi.kr"));
//    }
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("API Document")
                .version("v0.0.1")
                .description("REDDI API 명세서");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

}
