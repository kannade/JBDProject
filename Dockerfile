# FROM eclipse-temurin:17-jdk
# WORKDIR /app
# COPY build/libs/demo-0.0.1.jar .
# CMD ["java", "-jar", "demo-0.0.1.jar"]

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
