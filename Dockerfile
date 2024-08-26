FROM eclipse-temurin:21-jre

WORKDIR /app

COPY matvey/build/libs/matvey-all.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "matvey-all.jar", "prod"]
