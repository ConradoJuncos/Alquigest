FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY backend/pom.xml backend/
COPY backend/src backend/src
COPY backend/.mvn backend/.mvn
COPY backend/mvnw backend/
RUN cd backend && chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "-jar", "app.jar"]