# Базовый образ с JDK 17
FROM eclipse-temurin:17-jdk-jammy

# Рабочая директория
WORKDIR /app

# Копируем jar
COPY target/bankcards-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
