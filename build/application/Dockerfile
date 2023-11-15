ARG DIRIGIBLE_VERSION=latest
FROM amazoncorretto:21-alpine

VOLUME /tmp

RUN apk add nodejs npm
RUN npm i -g esbuild
RUN npm i -g typescript

COPY target/dirigible-application-*-executable.jar dirigible.jar
ENTRYPOINT ["java","-jar","/dirigible.jar"]
