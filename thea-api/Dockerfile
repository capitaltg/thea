FROM eclipse-temurin:17-jre-alpine

RUN adduser --disabled-password ctg
RUN mkdir /app
RUN chown ctg /app
USER ctg

COPY build/libs/thea-api.jar /app/thea-api.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/thea-api.jar"]
