FROM java:8

COPY docker_test-1.0-SNAPSHOT.jar /app.jar

CMD ["--server.port=8080"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]