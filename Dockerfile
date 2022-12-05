FROM openjdk:17-jdk-alpine3.14
LABEL maintainers="Tomasz Wazny"
ADD target/PoSzkole-0.0.1-SNAPSHOT.jar .
EXPOSE 80
CMD java -jar PoSzkole-0.0.1-SNAPSHOT.jar