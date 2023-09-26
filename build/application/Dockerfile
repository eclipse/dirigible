ARG DIRIGIBLE_VERSION=latest
FROM amazoncorretto:21

VOLUME /tmp

RUN curl -sL https://rpm.nodesource.com/setup_16.x | bash - 
RUN yum install -y nodejs 
RUN npm i -g esbuild
RUN npm i -g typescript

COPY target/*.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]