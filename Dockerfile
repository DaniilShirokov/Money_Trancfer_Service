FROM openjdk:17-alpine

WORKDIR /app

COPY target/money_transfer_service-0.0.1-SNAPSHOT.jar /app/

EXPOSE 5500

CMD ["java", "-jar", "/app/money_transfer_service-0.0.1-SNAPSHOT.jar"]