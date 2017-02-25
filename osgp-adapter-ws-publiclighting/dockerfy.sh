#!/bin/bash
COMPONENT=osgp-adapter-ws-publiclighting
docker build -t $COMPONENT ./
docker run -m 1024M -it --rm -e "DBHOST=10.0.2.15" -e "DBPORT=5432" -e "JMSHOST=10.0.2.15" -e "JMSPORT=61616" -p 8009:8009 --name $COMPONENT $COMPONENT
