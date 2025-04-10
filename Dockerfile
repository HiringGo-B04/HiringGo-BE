# ----------- Build Stage -----------
FROM amazoncorretto:21 AS builder

WORKDIR /app
COPY . .

# Build the app without running tests
RUN ./gradlew clean build -x test -x check

# ----------- Runtime Stage -----------
FROM amazoncorretto:21

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
