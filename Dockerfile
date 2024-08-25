FROM gradle:8.5.0-jdk21 as BUILD
RUN mkdir -p /app
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=BUILD /app/build/libs/bi_test-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]