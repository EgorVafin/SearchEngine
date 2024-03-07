FROM openjdk:21-jdk
WORKDIR /app
ARG JAR_FILE=target/*SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080 8443
ENTRYPOINT ["java", "-jar", "app.jar"]