FROM tomcat:7.0.73-jre8-alpine

MAINTAINER smart society services

ADD target/osgp-adapter-ws-core-4.6.0-SNAPSHOT.war /usr/local/tomcat/webapps/osgp-adapter-ws-core.war

ADD https://jdbc.postgresql.org/download/postgresql-9.4.1212.jar /usr/local/tomcat/lib
ADD https://github.com/OSGP/Config/raw/development/certificates/osgp-ca/certs/LianderNetManagement.pfx /etc/ssl/certs
ADD https://github.com/OSGP/Config/raw/development/certificates/osgp-ca/certs/test-org.pfx /etc/ssl/certs

CMD ["catalina.sh", "run"]
