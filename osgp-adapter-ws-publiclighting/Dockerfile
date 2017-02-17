FROM tomcat:7.0.73-jre8-alpine
ENV JAVA_OPTS="-Xms256m -Xmx768m"

MAINTAINER smart society services

ADD target/osgp-adapter-ws-publiclighting-4.6.0-SNAPSHOT.war /usr/local/tomcat/webapps/osgp-adapter-ws-publiclighting.war

ADD https://jdbc.postgresql.org/download/postgresql-9.4.1212.jar /usr/local/tomcat/lib
ADD https://github.com/OSGP/Config/raw/development/certificates/osgp-ca/certs/LianderNetManagement.pfx /etc/ssl/certs
ADD https://github.com/OSGP/Config/raw/development/certificates/osgp-ca/certs/test-org.pfx /etc/ssl/certs

RUN sed -i 's@<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />@<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" maxThreads="10000" maxConnections="8000" minSpareThreads="100" maxIdleTime="5000" />@' /usr/local/tomcat/conf/server.xml

CMD ["catalina.sh", "run"]
