ARG DIRIGIBLE_VERSION=latest
FROM amazoncorretto:20
VOLUME /tmp
COPY target/*.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]