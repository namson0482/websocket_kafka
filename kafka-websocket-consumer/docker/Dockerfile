FROM amazoncorretto:11-alpine-jdk

RUN apk --no-cache add curl

# https://github.com/ufoscout/docker-compose-wait
## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.10.0/wait /wait
RUN chmod +x /wait

## Launch the wait tool and then your application
CMD /wait

EXPOSE 8082 8082
RUN mkdir -p /temp
RUN chmod 777 /temp

RUN mkdir -p /scripts
RUN chmod 777 /scripts


COPY kafka-websocket-consumer-0.0.1-SNAPSHOT.jar kafka-websocket-consumer-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/kafka-websocket-consumer-0.0.1.jar"]
