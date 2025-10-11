package com.andreykhimchenko.bankacountmanagement.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Account Management API")
                        .version("1.0")
                        .description("Документация по REST API банковской системы управления счетами")
                        .contact(new Contact()
                                .name("Andrey Khimchenko")
                                .email("khimchenko_andrey@male.ru")
                        )
                );
    }
}
