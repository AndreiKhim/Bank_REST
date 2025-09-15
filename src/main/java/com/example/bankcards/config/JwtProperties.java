package com.example.bankcards.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;

@Component
public class JwtProperties {

    private final String secret;
    private final long expiration;

    public JwtProperties() {
        // Читаем переменные из окружения с дефолтными значениями
        this.secret = System.getenv().getOrDefault(
                "JWT_SECRET",
                "U29tZVNlY3VyZVJhbmRvbUtleUZvclRlc3RpbmcxMjM0NTY3ODkwMTIzNA=="
        );

        String expStr = System.getenv().getOrDefault("JWT_EXPIRATION", "86400000"); // 24 часа
        this.expiration = Long.parseLong(expStr);
    }

    public String getSecret() {
        return secret;
    }

    public long getExpiration() {
        return expiration;
    }
}