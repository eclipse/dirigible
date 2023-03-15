ARG DIRIGIBLE_VERSION=latest
FROM arm64v8/amazoncorretto:19
VOLUME /tmp
COPY target/*.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]