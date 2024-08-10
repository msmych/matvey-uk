FROM eclipse-temurin:22-jdk as builder

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY app /app/app
COPY drinki /app/drinki
COPY migraine /app/migraine
COPY telek /app/telek

RUN ./gradlew app:shadowJar

FROM eclipse-temurin:22-jre

WORKDIR /app

COPY --from=builder /app/app/build/libs/app-all.jar /app/app-all.jar

EXPOSE 8443

CMD ["java", "-jar", "app-all.jar", "prod"]
