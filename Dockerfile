FROM gradle:jdk21-alpine
ARG PRODUCTION
ARG JDBC_DATABASE_PASSWORD
ARG JDBC_DATABASE_URL
ARG JDBC_DATABASE_USERNAME

ENV PRODUCTION ${PRODUCTION}
ENV JDBC_DATABASE_PASSWORD ${JDBC_DATABASE_PASSWORD}
ENV JDBC_DATABASE_URL ${JDBC_DATABASE_URL}
ENV JDBC_DATABASE_USERNAME ${JDBC_DATABASE_USERNAME}

WORKDIR /app
COPY build/libs/Log-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java","-jar","Log-0.0.1-SNAPSHOT.jar"]

# # ----------- Build Stage -----------
# FROM amazoncorretto:21 AS builder

# WORKDIR /app
# COPY . .

# # Build the app without running tests
# RUN ./gradlew clean build -x test -x check

# # ----------- Runtime Stage -----------
# FROM amazoncorretto:21

# WORKDIR /app

# # Copy the built jar from the builder stage
# COPY --from=builder /app/build/libs/*.jar app.jar

# # Expose the default Spring Boot port
# EXPOSE 8080

# # Start the app
# ENTRYPOINT ["java", "-jar", "app.jar"]
