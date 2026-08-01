# Use a lightweight Java 21 runtime as the base image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built fat JAR from your target folder to the container
COPY target/spring357-eshop-1.0.0.jar app.jar

# Expose port 8080 (Spring Boot's default port)
EXPOSE 8080

# Command to execute the JAR when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]