# ==================================================
# MULTI-STAGE DOCKERFILE ДЛЯ SPRING BOOT ПРОЕКТУ
# Система обліку військової допомоги
# ==================================================

# ==================================================
# STAGE 1: BUILD
# Збірка проекту за допомогою Maven
# ==================================================
FROM maven:3.9.11-eclipse-temurin-17-alpine AS build

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо Maven конфігурацію та wrapper
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Завантажуємо залежності (окремий шар для кешування)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Копіюємо вихідний код
COPY src ./src

# Збираємо проект (пропускаємо тести для швидкості)
RUN ./mvnw clean package -DskipTests

# ==================================================
# STAGE 2: RUNTIME
# Легкий образ для запуску додатку
# ==================================================
FROM eclipse-temurin:17-jre-alpine

# Метадані образу
LABEL maintainer="yurets"
LABEL description="Military Aid System - Spring Boot Application"
LABEL version="1.4.0"

# Створюємо непривілейованого користувача
RUN addgroup -S spring && adduser -S spring -G spring

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо JAR файл зі stage build
COPY --from=build /app/target/*.jar app.jar

# Змінюємо власника файлів
RUN chown -R spring:spring /app

# Перемикаємось на непривілейованого користувача
USER spring:spring

# Відкриваємо порт додатку
EXPOSE 8080

# Налаштування JVM для контейнера
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/login || exit 1

# Точка входу
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]