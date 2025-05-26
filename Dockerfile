# Étape 1 : Build de l'application
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copier les fichiers nécessaires
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copier le JAR compilé depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
