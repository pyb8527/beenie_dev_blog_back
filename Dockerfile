# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM gradle:8.8-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle :core:bootJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
# bootJar produces core-<version>.jar (the -plain.jar is the non-executable one)
COPY --from=build /workspace/core/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
