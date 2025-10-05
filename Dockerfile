FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Copiar archivos del proyecto
COPY backend/pom.xml backend/
COPY backend/src backend/src

# Construir el proyecto
RUN cd backend && mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "-jar", "app.jar"]