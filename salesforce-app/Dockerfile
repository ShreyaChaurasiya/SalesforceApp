# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy maven files
COPY pom.xml .
COPY src ./src

# Install maven and build
RUN apk add --no-cache maven
RUN mvn clean install -DskipTests

# Run the jar
EXPOSE 8080
CMD ["java", "-jar", \
  "target/salesforce-app-0.0.1-SNAPSHOT.jar"]