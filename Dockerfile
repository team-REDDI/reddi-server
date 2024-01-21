FROM openjdk:17
ARG JAR_FILE=/build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} server.jar
ENTRYPOINT ["java", "-jar", "./server.jar"]