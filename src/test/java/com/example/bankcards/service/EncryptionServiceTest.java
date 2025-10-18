package com.example.bankcards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    void encryptAndDecrypt_shouldReturnOriginalValue() {
        String original = "1234567890123456";
        String encrypted = encryptionService.encrypt(original);
        String decrypted = encryptionService.decrypt(encrypted);

        assertThat(encrypted).isNotEqualTo(original);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void maskCardNumber_shouldReturnMaskedFormat() {
        String masked = encryptionService.maskCardNumber("1234567812345678");
        assertThat(masked).isEqualTo("**** **** **** 5678");
    }

    @Test
    void maskCardNumber_shouldHandleShortNumbers() {
        String masked = encryptionService.maskCardNumber("123");
        assertThat(masked).isEqualTo("****");
    }

    @Test
    void maskCardNumber_shouldHandleNull() {
        String masked = encryptionService.maskCardNumber(null);
        assertThat(masked).isEqualTo("****");
    }

    @Test
    void encrypt_shouldBeConsistent() {
        String first = encryptionService.encrypt("test");
        String second = encryptionService.encrypt("test");
        assertThat(first).isEqualTo(second); // Base64 — детерминированное шифрование
    }
}
