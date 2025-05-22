# ----------- Build Stage -----------
FROM gradle:jdk21-alpine AS builder

WORKDIR /app
COPY . .

# Build the app without running tests
RUN ./gradlew clean build -x test -x check

# ----------- Runtime Stage -----------
FROM gradle:jdk21-alpine

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]