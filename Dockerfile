# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Resolve dependencies first so they are cached between source changes
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --no-create-home app
USER app

COPY --from=build /workspace/target/workout-tracker-backend-*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=production
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
