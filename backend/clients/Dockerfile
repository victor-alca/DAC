# Usa uma imagem do Maven para buildar o projeto
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Usa uma imagem Java leve para rodar o app
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.war app.war
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "app.war"]