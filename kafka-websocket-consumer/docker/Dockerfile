FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.18_10

RUN apk --no-cache add curl

# https://github.com/ufoscout/docker-compose-wait
## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.10.0/wait /wait
RUN chmod +x /wait

EXPOSE 6363 6363
RUN mkdir -p /temp
RUN chmod 777 /temp

RUN mkdir -p /scripts
RUN chmod 777 /scripts

COPY kafka-websocket-consumer-0.0.1-SNAPSHOT.jar kafka-websocket-consumer-0.0.1.jar

## Launch the wait tool and then your application
CMD /wait && java -jar ./kafka-websocket-consumer-0.0.1.jar




#ENTRYPOINT ["java","-jar","/kafka-websocket-consumer-0.0.1.jar"]
