FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Oracle JDBC driver first
COPY src/ojdbc17.jar /app/lib/ojdbc17.jar

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

