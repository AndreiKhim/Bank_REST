package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.entity.enums.TransactionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static User createUser(Long id, String email) {
        try {
            Constructor<User> ctor = User.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            User user = ctor.newInstance();

            // устанавливаем id через reflection (т.к. сеттера может не быть)
            setId(user, id);

            // заполняем остальные поля через обычные сеттеры
            user.setEmail(email);
            user.setFirstName("Test");
            user.setLastName("User");
            user.setPassword("password"); // для тестов
            user.setRole(Role.USER);      // если в проекте Role — enum
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать User для теста", e);
        }
    }

    public static Card createCard(Long id, User user, BigDecimal balance) {
        try {
            Constructor<Card> ctor = Card.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            Card card = ctor.newInstance();

            setId(card, id);

            card.setCardNumberEncrypted("encrypted-" + id);
            card.setCardStatus(CardStatus.ACTIVE);
            card.setBalance(balance);
            card.setExpirationDate(LocalDate.now().plusYears(3));
            card.setUser(user);
            return card;
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать Card для теста", e);
        }
    }

    // ---- Вспомогательные методы ----

    private static void setId(Object obj, Long id) {
        try {
            Field f = findFieldRecursive(obj.getClass(), "id");
            f.setAccessible(true);
            f.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось установить id через reflection", e);
        }
    }

    private static Field findFieldRecursive(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Поле '" + name + "' не найдено в классе " + clazz.getName());
    }

    public static Transaction createTransaction(Card source, Card dest, BigDecimal amount) {
        return new Transaction(
                source,
                dest,
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "Test transaction"
        );
    }
}
