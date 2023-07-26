ARG DIRIGIBLE_VERSION=latest
FROM amazoncorretto:20

VOLUME /tmp

RUN curl -sL https://rpm.nodesource.com/setup_16.x | bash - 
RUN yum install -y nodejs 
RUN npm i -g esbuild

COPY target/*.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]