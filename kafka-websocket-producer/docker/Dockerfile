FROM amazoncorretto:11-alpine-jdk

## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.10.0/wait /wait
RUN chmod +x /wait

## Add your application to the docker image
ADD MySuperApp.sh /MySuperApp.sh

## Launch the wait tool and then your application
CMD /wait && /MySuperApp.sh


EXPOSE 8082 8082
RUN mkdir -p /temp
RUN chmod 777 /temp
COPY  data/* /temp/

RUN mkdir -p /scripts
RUN chmod 777 /scripts
COPY wait-for-it.sh /scripts

COPY kafka-websocket-producer-0.0.1-SNAPSHOT.jar kafka-websocket-producer-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/kafka-websocket-producer-0.0.1.jar"]
