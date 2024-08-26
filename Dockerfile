FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY matvey /app/matvey

RUN ./gradlew matvey:shadowJar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/app/build/libs/matvey-all.jar /app/matvey-all.jar

EXPOSE 8080

CMD ["java", "-jar", "matvey-all.jar", "prod"]
