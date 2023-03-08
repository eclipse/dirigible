FROM amazoncorretto:19
VOLUME /tmp
COPY target/*.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]