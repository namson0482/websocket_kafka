FROM amazoncorretto:11-alpine-jdk
EXPOSE 8082 8082
RUN mkdir -p /temp
RUN chmod 777 /temp
#COPY krb5.keytab /temp
#COPY krb5.conf /etc

COPY kafka-websocket-consumer-0.0.1-SNAPSHOT.jar kafka-websocket-consumer-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/kafka-websocket-consumer-0.0.1.jar"]
