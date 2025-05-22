# ----------- Build Stage -----------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

# Build the app without running tests
RUN ./gradlew clean build -x test -x check

# ----------- Runtime Stage -----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]