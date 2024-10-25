# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

LABEL authors="Andrei"

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build output to the container
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar

# Expose the port that the application will run on
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]

# Optional environment variables if needed
# ENV SPRING_PROFILES_ACTIVE=prod
