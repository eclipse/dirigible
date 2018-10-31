# Docker descriptor for Dirigible
# License - http://www.eclipse.org/legal/epl-v10.html
 
FROM tomcat:8.5.34-jre8
 
RUN rm -R /usr/local/tomcat/webapps/*
COPY server-all/target/ROOT.war $CATALINA_HOME/webapps/
RUN rm /usr/local/tomcat/conf/tomcat-users.xml
RUN wget http://www.dirigible.io/help/conf/tomcat-users.xml -P /usr/local/tomcat/conf/
RUN wget https://jdbc.postgresql.org/download/postgresql-42.1.4.jar -P /usr/local/tomcat/lib/
 
EXPOSE 8080
CMD ["catalina.sh", "run"]
