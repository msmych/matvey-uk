FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

ARG GH_PACKAGES_RO_TOKEN
ENV GH_PACKAGES_RO_TOKEN=$GH_PACKAGES_RO_TOKEN

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY matvey /app/matvey

RUN chmod +x gradlew

RUN ./gradlew matvey:shadowJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/app/build/libs/matvey-all.jar /app/matvey-all.jar
COPY --from=builder /app/app/build /app/build

EXPOSE 8080

CMD ["java", "-jar", "build/libs/matvey-all.jar", "prod"]
