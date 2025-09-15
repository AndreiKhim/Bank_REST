package com.example.bankcards.service;

import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class EncryptionService {

    // 🔑 простой вариант — Base64 (НЕ безопасный, но хватит для тестового задания)
    public String encrypt(String plainText) {
        return Base64.getEncoder().encodeToString(plainText.getBytes());
    }

    public String decrypt(String encryptedText) {
        return new String(Base64.getDecoder().decode(encryptedText));
    }

    // Для маскирования (оставляем 4 последние цифры)
    public String maskCardNumber(String plainNumber) {
        if (plainNumber == null || plainNumber.length() < 4) return "****";
        return "**** **** **** " + plainNumber.substring(plainNumber.length() - 4);
    }
}