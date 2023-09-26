ARG DIRIGIBLE_VERSION=latest
FROM amazoncorretto:21-alpine

VOLUME /tmp

RUN apk add nodejs npm
RUN npm i -g esbuild
RUN npm i -g typescript

COPY target/*.jar dirigible.jar
RUN jar -xf dirigible.jar
RUN rm -rf dirigible.jar
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
