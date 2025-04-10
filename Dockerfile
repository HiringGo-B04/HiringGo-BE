# ----------- Build Stage -----------
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app
COPY . .

# Optional debug: Check DB connectivity before building
RUN echo "Just checking if I can ping the database..." && ping -c 3 postgres.railway.internal || echo "Ping to postgres.railway.internal failed"

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
