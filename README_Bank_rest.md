# 🏦 Bank REST API

## 📘 Описание проекта

**Bank REST API** — это учебный backend-проект на **Java (Spring Boot)**, реализующий систему управления **банковскими картами, пользователями и транзакциями**.  
Система использует **JWT-аутентификацию**, разграничение прав по ролям (`USER`, `ADMIN`) и ведёт историю всех операций.

---

## ⚙️ Используемые технологии

| Компонент | Назначение |
|------------|------------|
| **Java 17+** | Язык разработки |
| **Spring Boot** | Основной фреймворк приложения |
| **Spring Security + JWT** | Аутентификация и авторизация |
| **Spring Data JPA (Hibernate)** | ORM для взаимодействия с MySQL |
| **MySQL** | Основная база данных |
| **Liquibase** | Миграции базы данных |
| **Lombok** | Сокращение boilerplate-кода |
| **OpenAPI / Swagger** | Документация REST API |

---

## 🗂️ Структура проекта

# 🏦 Bank REST API

## 📘 Описание проекта

**Bank REST API** — это учебный backend-проект на **Java (Spring Boot)**, реализующий систему управления **банковскими картами, пользователями и транзакциями**.  
Система использует **JWT-аутентификацию**, разграничение прав по ролям (`USER`, `ADMIN`) и ведёт историю всех операций.

---

## ⚙️ Используемые технологии

| Компонент | Назначение |
|------------|------------|
| **Java 17+** | Язык разработки |
| **Spring Boot** | Основной фреймворк приложения |
| **Spring Security + JWT** | Аутентификация и авторизация |
| **Spring Data JPA (Hibernate)** | ORM для взаимодействия с MySQL |
| **MySQL** | Основная база данных |
| **Liquibase** | Миграции базы данных |
| **Lombok** | Сокращение boilerplate-кода |
| **OpenAPI / Swagger** | Документация REST API |

---

## 🧩 Liquibase миграции

Главный changelog-файл:
```yaml
databaseChangeLog:
  - include:
      file: db/migration/changes/001-create-users-table.yaml
  - include:
      file: db/migration/changes/002-create-cards-table.yaml
  - include:
      file: db/migration/changes/003-create-transactions-table.yaml
  - include:
      file: db/migration/changes/004-change-cart-number-name.yaml


🧑‍💼 Роли и доступ
| Роль      | Возможности                                                     |
| --------- | --------------------------------------------------------------- |
| **ADMIN** | Управляет пользователями, картами, транзакциями                 |
| **USER**  | Управляет своими картами, делает переводы, просматривает баланс |

🔑 Аутентификация

JWT-токен используется для всех защищённых эндпоинтов.

Метод	Путь	Описание
POST	/api/auth/register	Регистрация нового пользователя
POST	/api/auth/login	Авторизация и получение JWT

Пример ответа при успешном входе:

{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Вход успешен",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER"
  }
}


Используй токен в заголовках:

Authorization: Bearer <token>

🧍 Пользователи (/api/users)
Метод	Путь	Описание
GET	/me	Получить данные своего профиля
PUT	/me	Обновить данные своего профиля

💳 Карты пользователя (/api/users/me/cards)
Метод	Путь	Описание
GET	/	Получить список своих карт
GET	/{cardId}/balance	Узнать баланс карты
PUT	/{cardId}/request-block	Запросить блокировку карты

🔁 Транзакции (/api/transactions)
Метод	Путь	Описание
GET	/my	Просмотр истории транзакций текущего пользователя
POST	/transfer	Перевод между своими картами
GET	/all (ADMIN)	Просмотр всех транзакций
GET	/user/{userId} (ADMIN)	Просмотр транзакций конкретного пользователя

🧑‍💼 Администратор — Пользователи (/api/admin/users)
Метод	Путь	Описание
GET	/	Получить всех пользователей
GET	/{id}	Получить пользователя по ID
POST	/	Создать нового пользователя
PUT	/{id}	Обновить пользователя
DELETE	/{id}	Удалить пользователя

🧾 Администратор — Карты (/api/admin/cards)
Метод	Путь	Описание
GET	/	Получить все карты
GET	/user/{userId}	Получить карты пользователя
POST	/{userId}	Создать карту для пользователя
PUT	/{cardId}/block	Заблокировать карту
PUT	/{cardId}/activate	Активировать карту
DELETE	/{cardId}	Удалить карту
🗄️ Настройка базы данных (MySQL)

Файл application.yml:

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bank_rest_db?useSSL=false&serverTimezone=UTC
    username: bestuser
    password: bestuser
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true

  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml

server:
  port: 8080


⚠️ Убедись, что база данных bank_rest_db создана заранее в MySQL.

🚀 Запуск проекта
🧰 Через IntelliJ IDEA

Убедись, что MySQL запущен.

Открой проект (pom.xml).

Проверь файл application.yml.

Запусти главный класс BankcardsApplication.

💻 Через Maven
mvn clean package
java -jar target/bankcards-0.0.1-SNAPSHOT.jar

🌐 Swagger UI

После запуска документация доступна по адресу:
http://localhost:8080/swagger-ui/index.html

🧱 Главный класс запуска

src/main/java/com/example/bankcards/BankcardsApplication.java
package com.example.bankcards;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankcardsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankcardsApplication.class, args);
        System.out.println("✅ Bank REST API успешно запущен на http://localhost:8080");
    }
}

📖 Автор проекта Химченко Андрей

Bank REST API — учебный проект, демонстрирующий:

создание REST-сервисов на Spring Boot

JWT-аутентификацию

ролевую авторизацию

ORM через JPA

контроль структуры БД через Liquibase

документацию API через Swagger


---
