# Docker descriptor for Dirigible
# License - http://www.eclipse.org/legal/epl-v10.html
 
FROM tomcat:8.5.8-jre8
 
RUN rm -R /usr/local/tomcat/webapps/*
COPY target/ROOT.war $CATALINA_HOME/webapps/
RUN rm /usr/local/tomcat/conf/tomcat-users.xml
RUN curl https://downloads.jboss.org/keycloak/4.0.0.Beta3/adapters/keycloak-oidc/keycloak-tomcat8-adapter-dist-4.0.0.Beta3.zip --create-dirs -o /usr/local/tomcat/lib/keycloak-tomcat8-adapter-dist.zip
RUN cd /usr/local/tomcat/lib && unzip keycloak-tomcat8-adapter-dist.zip
RUN wget https://jdbc.postgresql.org/download/postgresql-42.1.4.jar -P /usr/local/tomcat/lib/

EXPOSE 8080
CMD ["catalina.sh", "run"]
