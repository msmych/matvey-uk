FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

ARG GH_PACKAGES_RO_TOKEN
ENV GH_PACKAGES_RO_TOKEN=$GH_PACKAGES_RO_TOKEN

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY app /app/app
COPY corsa /app/corsa
COPY drinki /app/drinki
COPY migraine /app/migraine
COPY telek /app/telek

RUN ./gradlew app:shadowJar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/app/build/libs/app-all.jar /app/app-all.jar

EXPOSE 8443

CMD ["java", "-jar", "app-all.jar", "prod"]
