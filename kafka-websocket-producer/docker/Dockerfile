FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.18_10

## Add the wait script to the image
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.10.0/wait /wait
RUN chmod +x /wait


EXPOSE 6064 6064
RUN mkdir -p /temp
RUN chmod 777 /temp
COPY  data/* /temp/

RUN mkdir -p /scripts
RUN chmod 777 /scripts
# https://github.com/vishnubob/wait-for-it
COPY wait-for-it.sh /scripts
RUN chmod +x /scripts/wait-for-it.sh

RUN apk add --update bash && rm -rf /var/cache/apk/*

COPY kafka-websocket-producer-0.0.1-SNAPSHOT.jar kafka-websocket-producer-0.0.1.jar

## Launch the wait tool and then your application
CMD /wait && java -jar /kafka-websocket-producer-0.0.1.jar
