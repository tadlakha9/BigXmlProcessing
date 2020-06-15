FROM openjdk:8-jdk-alpine
EXPOSE 8090
WORKDIR /multiprocessor-app
ARG JAR_FILE=target/*.jar
ARG RESOURCES=src/main/resources/*
ARG SCRIPT_FILE=src/script/FileFormatter.ksh
COPY ${JAR_FILE} ./multiprocessor.jar
COPY ${RESOURCES} ./src/main/resources/
COPY ${SCRIPT_FILE} ./src/script/FileFormatter.ksh
ENTRYPOINT ["java","-jar","./multiprocessor.jar"]