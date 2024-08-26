FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY matvey/build/libs/matvey-all.jar /app/

EXPOSE 8080

CMD ["java", "-jar", "matvey-all.jar", "prod"]
