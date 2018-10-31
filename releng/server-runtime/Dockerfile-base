# Docker descriptor for Dirigible
# License - http://www.eclipse.org/legal/epl-v10.html

FROM tomcat:8.5.34-jre8

RUN rm -R /usr/local/tomcat/webapps/*
COPY target/ROOT.war $CATALINA_HOME/webapps/
RUN unzip $CATALINA_HOME/webapps/ROOT.war -d $CATALINA_HOME/webapps/ROOT
RUN rm $CATALINA_HOME/webapps/ROOT.war
RUN rm /usr/local/tomcat/conf/tomcat-users.xml
RUN wget http://www.dirigible.io/help/conf/tomcat-users.xml -P /usr/local/tomcat/conf/
RUN wget https://jdbc.postgresql.org/download/postgresql-42.1.4.jar -P /usr/local/tomcat/lib/