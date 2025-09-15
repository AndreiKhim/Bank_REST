package com.example.bankcards.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {

    private final Dotenv dotenv;

    public EnvConfig() {
        dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }

    public String get(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    public String get(String key) {
        return dotenv.get(key);
    }
}
