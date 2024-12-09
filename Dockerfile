# Build stage
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .

# Download dependencies and plugins (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app/logs && \
    chown -R spring:spring /app

# Install required packages
RUN apk add --no-cache tzdata curl

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring app.jar

# Set timezone and user
ENV TZ=UTC
USER spring

# Java options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+OptimizeStringConcat \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Container configuration
EXPOSE 8080

# Start application with proper memory settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
