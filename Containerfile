# Build stage
FROM maven:3.10-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:25-jre
RUN groupadd --system helpdeskgroup && \
    useradd --system --gid helpdeskgroup --no-create-home --shell /usr/sbin/nologin helpdeskuser
WORKDIR /app
COPY --chown=helpdeskuser:helpdeskgroup --from=build /app/target/helpdesk-1.0.0.jar app.jar
USER helpdeskuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
