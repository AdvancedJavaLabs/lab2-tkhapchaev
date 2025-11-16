FROM gradle:8.6-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/libs/*.jar /app/service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]