FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts /app/

COPY gradle /app/gradle
COPY app /app/app
COPY drinki /app/drinki
COPY dukt /app/dukt
COPY postal /app/postal
COPY telek /app/telek

RUN ./gradlew app:shadowJar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/app/build/libs/app-all.jar /app/app-all.jar

EXPOSE 8080

CMD ["java", "-jar", "app-all.jar"]
