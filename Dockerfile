FROM gradle:8.9-jdk21 as builder

WORKDIR /app

ARG GH_PACKAGES_RO_TOKEN
ENV GH_PACKAGES_RO_TOKEN=$GH_PACKAGES_RO_TOKEN

COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties /app/

COPY gradle /app/gradle
COPY matvey /app/matvey

RUN chmod +x gradlew

RUN ./gradlew matvey:build --no-daemon

EXPOSE 8080

CMD ["./gradlew", "matvey:run", "--no-daemon", "--args=\"prod\""]
