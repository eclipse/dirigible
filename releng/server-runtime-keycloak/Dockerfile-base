# Docker descriptor for Dirigible
# License - http://www.eclipse.org/legal/epl-v10.html

ARG DIRIGIBLE_VERSION=latest
FROM dirigiblelabs/dirigible-base-platform-runtime:$DIRIGIBLE_VERSION

RUN rm /usr/local/tomcat/conf/tomcat-users.xml
RUN curl https://downloads.jboss.org/keycloak/4.0.0.Beta3/adapters/keycloak-oidc/keycloak-tomcat8-adapter-dist-4.0.0.Beta3.zip --create-dirs -o /usr/local/tomcat/lib/keycloak-tomcat8-adapter-dist.zip
RUN cd /usr/local/tomcat/lib && unzip keycloak-tomcat8-adapter-dist.zip
RUN wget https://jdbc.postgresql.org/download/postgresql-42.1.4.jar -P /usr/local/tomcat/lib/
COPY src/main/webapp/META-INF/context.xml /usr/local/tomcat/webapps/ROOT/META-INF/
COPY src/main/webapp/WEB-INF/keycloak.json /usr/local/tomcat/webapps/ROOT/WEB-INF/
COPY src/main/webapp/WEB-INF/keycloak.json /usr/local/tomcat/webapps/ROOT/WEB-INF/