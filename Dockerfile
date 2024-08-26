FROM eclipse-temurin:21-jre

WORKDIR /app

COPY matvey/build/libs/app-all.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "app-all.jar", "prod"]
