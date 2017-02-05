FROM tomcat:7.0.73-jre8-alpine

LABEL maintainer "info@smartsocietyservices.com"

ENV JAVA_OPTS="-server \
      -Xms128M \
      -Xmx512M \
      -Djava.awt.headless=true \
      -XX:HeapDumpPath=/usr/local/tomcat/logs/java_heapdump_pid_%p.log"

ADD target/*.war /usr/local/tomcat/webapps

ADD https://jdbc.postgresql.org/download/postgresql-9.4.1212.jar /usr/local/tomcat/lib

CMD ["catalina.sh", "run"]
