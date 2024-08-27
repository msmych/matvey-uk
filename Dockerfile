FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

ARG GH_PACKAGES_RO_TOKEN
ENV GH_PACKAGES_RO_TOKEN=$GH_PACKAGES_RO_TOKEN

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY matvey /app/matvey

RUN chmod +x gradlew

RUN ./gradlew matvey:build --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/matvey /app/matvey
COPY --from=builder /app/gradle /app/gradle
COPY --from=builder /app/gradlew /app/
COPY --from=builder /app/settings.gradle.kts /app/
COPY --from=builder /app/build.gradle.kts /app/
COPY --from=builder /app/gradle.properties /app/

EXPOSE 8080

CMD ["./gradlew", "matvey:run", "--no-daemon", "--args=\"prod\""]
