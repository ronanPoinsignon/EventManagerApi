FROM openjdk:26-ea-jdk

WORKDIR /.
COPY . .
RUN ./gradlew bootJar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
